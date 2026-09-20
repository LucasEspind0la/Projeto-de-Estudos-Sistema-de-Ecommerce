import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreateCategoryRequest {
  nome: string;
  descricao?: string;
}

export interface UpdateCategoryRequest {
  nome?: string;
  descricao?: string;
}

export interface Category {
  id: number;
  nome: string;
  descricao: string | null;
  criadoEm: string;
}

@Injectable({ providedIn: 'root' })
export class CategoryService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>('/api/categorias');
  }

  getById(id: number): Observable<Category> {
    return this.http.get<Category>(`/api/categorias/${id}`);
  }

  create(payload: CreateCategoryRequest): Observable<Category> {
    return this.http.post<Category>('/api/categorias', payload);
  }

  update(id: number, payload: UpdateCategoryRequest): Observable<Category> {
    return this.http.put<Category>(`/api/categorias/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/categorias/${id}`);
  }
}