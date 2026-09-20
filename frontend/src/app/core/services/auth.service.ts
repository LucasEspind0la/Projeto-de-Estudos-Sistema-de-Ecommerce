import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface AuthResponse {
  token: string;
  email: string;
  papel: string;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface CadastroRequest {
  nome: string;
  email: string;
  senha: string;
  papel: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'auth_token';
  private userKey = 'auth_user';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', payload).pipe(
      tap((res) => this.setSession(res))
    );
  }

  /**
   * Realiza o cadastro de um novo usuário.
   * Por segurança, o papel é sempre definido como 'CLIENTE' no frontend.
   */
  register(payload: CadastroRequest): Observable<AuthResponse> {
    // Força o papel para CLIENTE, impedindo que usuários se cadastrem como ADMIN
    const payloadSeguro = { ...payload, papel: 'CLIENTE' };
    
    return this.http.post<AuthResponse>('/api/auth/cadastrar', payloadSeguro).pipe(
      tap((res) => this.setSession(res))
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return this.hasToken();
  }

  isAdmin(): boolean {
    const user = this.getUser();
    return user?.papel === 'ADMINISTRADOR';
  }

  getUser(): { email: string, papel: string } | null {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }

  private setSession(authResult: AuthResponse): void {
    localStorage.setItem(this.tokenKey, authResult.token);
    localStorage.setItem(this.userKey, JSON.stringify({ email: authResult.email, papel: authResult.papel }));
    this.isAuthenticatedSubject.next(true);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }
}
