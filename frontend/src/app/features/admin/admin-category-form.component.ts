import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { CategoryService, Category } from '../../core/services/category.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="form-container">
      <header class="header">
        <h1>{{ isEditMode ? 'Editar Categoria' : 'Cadastrar Nova Categoria' }}</h1>
        <button class="secondary-btn" routerLink="/admin/categorias">Voltar</button>
      </header>

      <form [formGroup]="categoryForm" (ngSubmit)="onSubmit()" class="category-form" *ngIf="!loadingData">
        <div class="form-group">
          <label>Nome da Categoria *</label>
          <input type="text" formControlName="nome" placeholder="Ex: Eletrônicos, Roupas, Alimentos">
          <div *ngIf="categoryForm.get('nome')?.invalid && categoryForm.get('nome')?.touched" class="error-msg">
            Nome é obrigatório (mín. 3 caracteres).
          </div>
        </div>

        <div class="form-group">
          <label>Descrição (Opcional)</label>
          <textarea formControlName="descricao" rows="4" placeholder="Descreva a categoria..."></textarea>
        </div>

        <div *ngIf="errorMessage" class="alert error">{{ errorMessage }}</div>

        <div class="form-actions">
          <button type="button" class="secondary-btn" routerLink="/admin/categorias" [disabled]="isSubmitting">Cancelar</button>
          <button type="submit" class="primary-btn" [disabled]="categoryForm.invalid || isSubmitting">
            {{ isSubmitting ? 'Processando...' : (isEditMode ? 'Atualizar Categoria' : 'Salvar Categoria') }}
          </button>
        </div>
      </form>
      <div *ngIf="loadingData" class="loading">Carregando dados...</div>
    </div>
  `,
    styles: [`
    .form-container { padding: 2rem; max-width: 800px; margin: 0 auto; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; background: #fff; min-height: 100vh; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #f0f0f0; }
    .header h1 { color: #111; margin: 0; font-size: 1.5rem; font-weight: 700; letter-spacing: -0.5px; }
    .secondary-btn { padding: 0.5rem 1rem; background: #fff; color: #111; border: 1px solid #ddd; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: border-color 0.2s; }
    .secondary-btn:hover { border-color: #111; }
    .secondary-btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .category-form { background: white; padding: 2rem; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }
    .form-group { margin-bottom: 1.5rem; }
    label { display: block; margin-bottom: 0.5rem; color: #111; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
    input, textarea { width: 100%; padding: 0.85rem 1rem; border: 1.5px solid #e5e5e5; border-radius: 12px; font-size: 1rem; box-sizing: border-box; background: #fafafa; transition: border-color 0.2s, background 0.2s; }
    input:focus, textarea:focus { outline: none; border-color: #111; background: #fff; }
    .error-msg { color: #e74c3c; font-size: 0.8rem; margin-top: 0.3rem; font-weight: 600; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 2rem; }
    .primary-btn { padding: 0.85rem 2rem; background: #111; color: white; border: none; border-radius: 30px; cursor: pointer; font-weight: 700; font-size: 1rem; transition: background 0.2s, transform 0.15s; }
    .primary-btn:hover:not(:disabled) { background: #333; transform: translateY(-1px); }
    .primary-btn:disabled { background: #bbb; cursor: not-allowed; }
    .alert { padding: 1rem; border-radius: 12px; margin-bottom: 1rem; text-align: center; font-weight: 600; font-size: 0.9rem; }
    .error { background: #fdecea; color: #c0392b; border: 1px solid #f5c6cb; }
    .loading { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
  `]
})
export class AdminCategoryFormComponent implements OnInit {
  categoryForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  
  isEditMode = false;
  categoryId: number | null = null;
  loadingData = false;

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.categoryForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      descricao: ['']
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/login']);
      return;
    }

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.categoryId = +idParam;
      this.loadCategoryData(this.categoryId);
    }
  }

  loadCategoryData(id: number): void {
    this.loadingData = true;
    this.categoryService.getById(id).subscribe({
      next: (category) => {
        this.categoryForm.patchValue({
          nome: category.nome,
          descricao: category.descricao || ''
        });
        this.loadingData = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar dados da categoria.';
        this.loadingData = false;
      }
    });
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const payload = {
      nome: this.categoryForm.get('nome')?.value,
      descricao: this.categoryForm.get('descricao')?.value || null
    };

    const request$ = this.isEditMode && this.categoryId
      ? this.categoryService.update(this.categoryId, payload)
      : this.categoryService.create(payload);

    request$.subscribe({
      next: () => {
        this.isSubmitting = false;
        alert(this.isEditMode ? 'Categoria atualizada com sucesso!' : 'Categoria cadastrada com sucesso!');
        this.router.navigate(['/admin/categorias']);
      },
      error: (err) => {
        this.isSubmitting = false;
        const msg = err.error?.mensagem || err.error?.erro || 'Erro ao salvar categoria. Verifique os dados.';
        this.errorMessage = msg;
        console.error('Erro:', err);
      }
    });
  }
}
