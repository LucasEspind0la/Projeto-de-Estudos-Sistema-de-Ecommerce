import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CategoryService, Category } from '../../core/services/category.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="admin-container">
      <header class="header">
        <h1>Gerenciamento de Categorias</h1>
        <div class="header-actions">
          <button class="secondary-btn" routerLink="/admin/dashboard">📊 Dashboard</button>
          <button class="primary-btn" routerLink="/admin/categorias/nova">+ Nova Categoria</button>
          <button class="secondary-btn" routerLink="/admin/produtos">Ver Produtos</button>
          <button class="logout-btn" (click)="logout()">Sair</button>
        </div>
      </header>

      <div *ngIf="loading" class="loading">Carregando categorias...</div>

      <div *ngIf="!loading && categories.length > 0" class="table-container">
        <table class="categories-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Criada em</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let category of categories">
              <td>{{ category.id }}</td>
              <td>{{ category.nome }}</td>
              <td>{{ category.descricao || '-' }}</td>
              <td>{{ category.criadoEm | date:'dd/MM/yyyy HH:mm' }}</td>
              <td class="actions">
                <button class="btn-edit" routerLink="/admin/categorias/editar/{{ category.id }}">Editar</button>
                <button class="btn-delete" (click)="deleteCategory(category.id)">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div *ngIf="!loading && categories.length === 0" class="empty">
        <p>Nenhuma categoria cadastrada.</p>
        <button class="primary-btn" routerLink="/admin/categorias/nova">Criar Primeira Categoria</button>
      </div>
    </div>
  `,
    styles: [`
    .admin-container { padding: 2rem; max-width: 1200px; margin: 0 auto; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; background: #fff; min-height: 100vh; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #f0f0f0; }
    .header h1 { color: #111; margin: 0; font-size: 1.6rem; font-weight: 700; letter-spacing: -0.5px; }
    .header-actions { display: flex; gap: 0.75rem; flex-wrap: wrap; }
    .primary-btn { padding: 0.5rem 1rem; background: #111; color: white; border: none; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: background 0.2s; }
    .primary-btn:hover { background: #333; }
    .secondary-btn { padding: 0.5rem 1rem; background: #fff; color: #111; border: 1px solid #ddd; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: border-color 0.2s; }
    .secondary-btn:hover { border-color: #111; }
    .logout-btn { padding: 0.5rem 1rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-weight: 600; font-size: 0.85rem; transition: all 0.2s; }
    .logout-btn:hover { background: #e74c3c; color: #fff; }
    .table-container { background: white; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); overflow-x: auto; }
    .categories-table { width: 100%; border-collapse: collapse; }
    .categories-table th, .categories-table td { padding: 1rem; text-align: left; border-bottom: 1px solid #f5f5f5; font-size: 0.9rem; }
    .categories-table th { background: #f6f6f6; color: #111; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .actions { display: flex; gap: 0.5rem; }
    .btn-edit { padding: 0.4rem 0.9rem; background: #fff; color: #f39c12; border: 1px solid #f39c12; border-radius: 20px; cursor: pointer; font-size: 0.8rem; font-weight: 600; transition: all 0.2s; }
    .btn-edit:hover { background: #f39c12; color: #fff; }
    .btn-delete { padding: 0.4rem 0.9rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-size: 0.8rem; font-weight: 600; transition: all 0.2s; }
    .btn-delete:hover { background: #e74c3c; color: #fff; }
    .loading, .empty { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
    .empty p { margin-bottom: 1.5rem; color: #888; }
  `]
})
export class AdminCategoriesComponent implements OnInit {
  categories: Category[] = [];
  loading = true;

  constructor(
    private categoryService: CategoryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar categorias:', err);
        this.loading = false;
      }
    });
  }

  deleteCategory(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta categoria? Produtos vinculados podem ser afetados.')) {
      this.categoryService.delete(id).subscribe({
        next: () => {
          this.categories = this.categories.filter(c => c.id !== id);
        },
        error: (err) => {
          console.error('Erro ao excluir categoria:', err);
          alert('Não foi possível excluir. A categoria pode estar vinculada a produtos.');
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}