import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ProductService, Product } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink],
  template: `
    <div class="admin-container">
      <header class="header">
        <h1>Gerenciamento de Produtos</h1>
        <div class="header-actions">
          <button class="secondary-btn" routerLink="/admin/dashboard"> Dashboard</button>
          <button class="primary-btn" routerLink="/admin/produtos/novo">+ Novo Produto</button>
          <button class="secondary-btn" routerLink="/admin/categorias">Gerenciar Categorias</button>
          <button class="secondary-btn" routerLink="/produtos">Ver Loja</button>
          <button class="logout-btn" (click)="logout()">Sair</button>
        </div>
      </header>

      <div *ngIf="loading" class="loading">Carregando...</div>

      <div *ngIf="!loading && products.length > 0" class="table-container">
        <table class="products-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Categoria</th>
              <th>Preço Mín.</th>
              <th>Estoque</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let product of products">
              <td>{{ product.id }}</td>
              <td>{{ product.nome }}</td>
              <td>{{ product.categoriaNome || '-' }}</td>
              <td>{{ getLowestPrice(product.variantes) | currency:'BRL':'symbol':'1.2-2' }}</td>
              <td>{{ getTotalStock(product.variantes) }}</td>
              <td>
                <span class="badge" [class.active]="product.ativo" [class.inactive]="!product.ativo">
                  {{ product.ativo ? 'Ativo' : 'Inativo' }}
                </span>
              </td>
              <td class="actions">
                <button class="btn-toggle" (click)="toggleActive(product)" title="Alternar Status">
                  {{ product.ativo ? 'Desativar' : 'Ativar' }}
                </button>
                <button class="btn-edit" routerLink="/admin/produtos/editar/{{ product.id }}">Editar</button>
                <button class="btn-delete" (click)="deleteProduct(product.id)">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div *ngIf="!loading && products.length === 0" class="empty">Nenhum produto cadastrado.</div>
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
    .products-table { width: 100%; border-collapse: collapse; }
    .products-table th, .products-table td { padding: 1rem; text-align: left; border-bottom: 1px solid #f5f5f5; font-size: 0.9rem; }
    .products-table th { background: #f6f6f6; color: #111; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .badge { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
    .badge.active { background: #eafaf1; color: #27ae60; }
    .badge.inactive { background: #fdecea; color: #c0392b; }
    .actions { display: flex; gap: 0.5rem; }
    .btn-toggle { padding: 0.4rem 0.9rem; background: #fff; color: #111; border: 1px solid #111; border-radius: 20px; cursor: pointer; font-size: 0.8rem; font-weight: 600; transition: all 0.2s; }
    .btn-toggle:hover { background: #111; color: #fff; }
    .btn-edit { padding: 0.4rem 0.9rem; background: #fff; color: #f39c12; border: 1px solid #f39c12; border-radius: 20px; cursor: pointer; font-size: 0.8rem; font-weight: 600; transition: all 0.2s; }
    .btn-edit:hover { background: #f39c12; color: #fff; }
    .btn-delete { padding: 0.4rem 0.9rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-size: 0.8rem; font-weight: 600; transition: all 0.2s; }
    .btn-delete:hover { background: #e74c3c; color: #fff; }
    .loading, .empty { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
  `]
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  loading = true;

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (data) => { this.products = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  getLowestPrice(variants: any[]): number {
    if (!variants?.length) return 0;
    return Math.min(...variants.map((v: any) => v.preco));
  }

  getTotalStock(variants: any[]): number {
    if (!variants) return 0;
    return variants.reduce((sum: number, v: any) => sum + v.estoque, 0);
  }

  toggleActive(product: Product): void {
    if (confirm(`Deseja ${product.ativo ? 'desativar' : 'ativar'} este produto?`)) {
      this.productService.toggleActive(product.id).subscribe({
        next: () => {
          product.ativo = !product.ativo;
        },
        error: (err) => console.error('Erro ao alternar status:', err)
      });
    }
  }

  deleteProduct(id: number): void {
    if (confirm('Tem certeza que deseja excluir este produto? Esta ação não pode ser desfeita.')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== id);
        },
        error: (err) => {
          console.error('Erro ao excluir:', err);
          alert('Não foi possível excluir. O produto pode estar vinculado a um pedido.');
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
