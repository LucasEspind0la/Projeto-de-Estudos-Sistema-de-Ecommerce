import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <div class="header">
          <h1>🛒 Sualoja</h1>
          <p class="subtitle">{{ isRegisterMode ? 'Crie sua conta para começar a comprar' : 'Acesse sua conta para continuar' }}</p>
        </div>

        <!-- Botões de Alternância -->
        <div class="toggle-container">
          <button 
            class="toggle-btn" 
            [class.active]="!isRegisterMode" 
            (click)="toggleMode(false)">
            Entrar
          </button>
          <button 
            class="toggle-btn" 
            [class.active]="isRegisterMode" 
            (click)="toggleMode(true)">
            Criar Conta
          </button>
        </div>

        <form [formGroup]="authForm" (ngSubmit)="onSubmit()" class="auth-form">
          
          <!-- Campo Nome (Apenas no Cadastro) -->
          <div class="form-group" *ngIf="isRegisterMode">
            <label>Nome Completo</label>
            <input type="text" formControlName="nome" placeholder="Seu nome">
            <div *ngIf="authForm.get('nome')?.invalid && authForm.get('nome')?.touched" class="error-msg">Nome é obrigatório.</div>
          </div>

          <div class="form-group">
            <label>Email</label>
            <input type="email" formControlName="email" placeholder="seu@email.com">
            <div *ngIf="authForm.get('email')?.invalid && authForm.get('email')?.touched" class="error-msg">Email inválido.</div>
          </div>

          <div class="form-group">
            <label>Senha</label>
            <input type="password" formControlName="senha" placeholder="••••••••">
            <div *ngIf="authForm.get('senha')?.invalid && authForm.get('senha')?.touched" class="error-msg">Mínimo de 6 caracteres.</div>
          </div>

          <div *ngIf="errorMessage" class="alert error">{{ errorMessage }}</div>

          <button type="submit" class="submit-btn" [disabled]="authForm.invalid || isSubmitting">
            {{ isSubmitting ? 'Processando...' : (isRegisterMode ? 'Cadastrar' : 'Entrar') }}
          </button>
        </form>

        <div class="footer">
          <p *ngIf="!isRegisterMode">
            Ainda não tem conta? <a (click)="toggleMode(true)">Cadastre-se grátis</a>
          </p>
          <p *ngIf="isRegisterMode">
            Já tem uma conta? <a (click)="toggleMode(false)">Faça login</a>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f6f6f6; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; padding: 1rem; }
    .login-card { background: white; padding: 2.5rem; border-radius: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); width: 100%; max-width: 420px; }
    .header { text-align: center; margin-bottom: 1.75rem; }
    .header h1 { margin: 0; color: #111; font-size: 2rem; font-weight: 700; letter-spacing: -0.5px; }
    .subtitle { color: #888; margin-top: 0.5rem; font-size: 0.9rem; }
    
    .toggle-container { display: flex; background: #f1f1f1; border-radius: 30px; padding: 4px; margin-bottom: 1.5rem; }
    .toggle-btn { flex: 1; padding: 0.6rem; border: none; background: transparent; border-radius: 30px; cursor: pointer; font-weight: 600; color: #888; transition: all 0.2s; }
    .toggle-btn.active { background: #111; color: #fff; }
    
    .auth-form { display: flex; flex-direction: column; gap: 1rem; }
    .form-group { display: flex; flex-direction: column; }
    label { font-size: 0.8rem; font-weight: 700; color: #111; margin-bottom: 0.4rem; letter-spacing: 0.3px; }
    input { padding: 0.85rem 1rem; border: 1.5px solid #e5e5e5; border-radius: 12px; font-size: 1rem; transition: border-color 0.2s; background: #fafafa; }
    input:focus { outline: none; border-color: #111; background: #fff; }
    .error-msg { color: #e74c3c; font-size: 0.75rem; margin-top: 0.3rem; font-weight: 600; }
    
    .submit-btn { padding: 0.95rem; background: #111; color: white; border: none; border-radius: 30px; font-size: 1rem; font-weight: 700; cursor: pointer; transition: background 0.2s, transform 0.15s; margin-top: 0.5rem; }
    .submit-btn:hover:not(:disabled) { background: #333; transform: translateY(-1px); }
    .submit-btn:disabled { background: #bbb; cursor: not-allowed; }
    
    .footer { text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: #888; }
    .footer a { color: #111; cursor: pointer; font-weight: 700; text-decoration: underline; text-underline-offset: 3px; }
    .footer a:hover { opacity: 0.7; }
    
    .alert { padding: 0.8rem; border-radius: 12px; font-size: 0.85rem; text-align: center; font-weight: 600; }
    .error { background: #fdecea; color: #c0392b; border: 1px solid #f5c6cb; }
  `]
})
export class LoginComponent {
  authForm: FormGroup;
  isRegisterMode = false;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.authForm = this.fb.group({
      nome: [''],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  toggleMode(isRegister: boolean): void {
    this.isRegisterMode = isRegister;
    this.errorMessage = '';
    this.authForm.reset();
    
    // Ajusta validações baseado no modo
    const nomeControl = this.authForm.get('nome');
    if (isRegister) {
      nomeControl?.setValidators([Validators.required, Validators.minLength(3)]);
    } else {
      nomeControl?.clearValidators();
    }
    nomeControl?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.authForm.invalid) {
      this.authForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const { email, senha, nome } = this.authForm.value;

    const request$ = this.isRegisterMode
      ? this.authService.register({ nome, email, senha, papel: 'CLIENTE' })
      : this.authService.login({ email, senha });

    request$.subscribe({
      next: () => {
        this.isSubmitting = false;
        // Redireciona baseado no papel do usuário
        if (this.authService.isAdmin()) {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/produtos']);
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        if (err.status === 401) {
          this.errorMessage = 'Email ou senha incorretos.';
        } else if (err.status === 400) {
          this.errorMessage = err.error?.mensagem || 'Dados inválidos. Verifique os campos.';
        } else {
          this.errorMessage = 'Erro ao conectar com o servidor.';
        }
        console.error('Erro de autenticação:', err);
      }
    });
  }
}