import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ProductService, Product } from '../../core/services/product.service';
import { CartService, AddToCartRequest } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { HeroCarouselComponent } from '../shared/hero-carousel.component';

export interface ProductUI extends Product {
  isAdding?: boolean;
  uiSuccessMessage?: string;
  uiErrorMessage?: string;
}

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterLink, HeroCarouselComponent],
  template: `
    <div class="products-container">
      <header class="header">
        <h1>Minha Loja :) </h1>
        <div class="header-actions">
          
          <!-- BOTÃO ADMIN: Agora usa a variável local 'isAdmin' -->
          <button *ngIf="isAdmin" class="admin-btn" routerLink="/admin/produtos">️ Admin</button>
          
          <button class="orders-btn" routerLink="/pedidos"> Meus Pedidos</button>
          <button class="cart-btn" routerLink="/carrinho">🛒 Carrinho</button>
          <button class="logout-btn" (click)="logout()">Sair</button>
        </div>
      </header>

      <!-- HERO CAROUSEL -->
      <app-hero-carousel></app-hero-carousel>

      <div *ngIf="loading" class="loading">Carregando produtos...</div>
      
      <div *ngIf="!loading && products.length === 0" class="empty">
        Nenhum produto ativo encontrado.
      </div>

      <div class="products-grid">
        <div *ngFor="let product of products; let i = index" class="product-card">

          <div class="product-image">
            <img 
              *ngIf="product.imagemUrl; else noImage" 
              [src]="product.imagemUrl" 
              [alt]="product.nome"
            >
            <ng-template #noImage>
              <div class="no-image">Sem Imagem</div>
            </ng-template>
          </div>

          <div class="product-info">
            <h3>{{ product.nome }}</h3>
            <p class="description">{{ product.descricao }}</p>
            
            <div class="price-section">
              <span class="price-label">A partir de</span>
              <span class="price">
                {{ getLowestPrice(product.variantes) | currency:'BRL':'symbol':'1.2-2' }}
              </span>
            </div>

            <div class="product-actions">
              <button 
                class="buy-now-btn" 
                (click)="buyNow(product)"
                [disabled]="product.isAdding"
              >
                {{ product.isAdding ? 'Aguarde...' : 'Comprar Agora' }}
              </button>
              <button 
                class="add-btn" 
                (click)="addToCart(product)"
                [disabled]="product.isAdding"
                title="Adicionar ao carrinho"
              >
                🛒
              </button>
            </div>

            <p *ngIf="product.uiSuccessMessage" class="success-msg">{{ product.uiSuccessMessage }}</p>
            <p *ngIf="product.uiErrorMessage" class="error-msg">{{ product.uiErrorMessage }}</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .products-container { padding: 2rem; max-width: 1200px; margin: 0 auto; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; background: #fff; min-height: 100vh; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2.5rem; padding-bottom: 1rem; border-bottom: 1px solid #f0f0f0; }
    .header h1 { color: #111; margin: 0; font-size: 1.6rem; font-weight: 700; letter-spacing: -0.5px; }
    .header-actions { display: flex; gap: 0.75rem; }

    .admin-btn { padding: 0.5rem 1rem; background: #111; color: white; border: none; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: background 0.2s; }
    .admin-btn:hover { background: #333; }
    .orders-btn { padding: 0.5rem 1rem; background: #fff; color: #111; border: 1px solid #ddd; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: border-color 0.2s; }
    .orders-btn:hover { border-color: #111; }
    .cart-btn { padding: 0.5rem 1rem; background: #fff; color: #111; border: 1px solid #ddd; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: border-color 0.2s; }
    .cart-btn:hover { border-color: #111; }
    .logout-btn { padding: 0.5rem 1rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-weight: 600; font-size: 0.85rem; transition: all 0.2s; }
    .logout-btn:hover { background: #e74c3c; color: #fff; }

    .products-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 2rem; }

    /* ===== CARD ESTILO NIKE ===== */
    .product-card {
      position: relative;
      background: #fff;
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06);
      overflow: hidden;
      transition: transform 0.25s ease, box-shadow 0.25s ease;
    }
    .product-card:hover { transform: translateY(-6px); box-shadow: 0 12px 30px rgba(0,0,0,0.12); }

    .badge {
      position: absolute;
      top: 14px;
      left: 14px;
      background: #111;
      color: #fff;
      font-size: 0.7rem;
      font-weight: 700;
      padding: 4px 10px;
      border-radius: 20px;
      letter-spacing: 0.5px;
      z-index: 2;
    }

    .product-image { width: 100%; height: 240px; background: #f6f6f6; display: flex; align-items: center; justify-content: center; overflow: hidden; }
    .product-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s ease; }
    .product-card:hover .product-image img { transform: scale(1.05); }
    .no-image { color: #bbb; font-size: 0.9rem; }

    .product-info { padding: 1.25rem 1.25rem 1.5rem; }
    .product-info h3 { margin: 0 0 0.35rem 0; color: #111; font-size: 1.05rem; font-weight: 600; }
    .description { color: #888; font-size: 0.85rem; margin-bottom: 1rem; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

    .price-section { display: flex; align-items: baseline; gap: 0.4rem; margin-bottom: 1rem; }
    .price-label { color: #888; font-size: 0.8rem; }
    .price { font-size: 1.3rem; font-weight: 700; color: #111; }

    .product-actions { display: flex; gap: 0.6rem; }

    /* Botão principal preto (estrela do layout) */
    .buy-now-btn {
      flex: 1;
      padding: 0.8rem;
      background: #111;
      color: #fff;
      border: none;
      border-radius: 30px;
      font-weight: 700;
      font-size: 0.9rem;
      cursor: pointer;
      transition: background 0.2s, transform 0.15s;
    }
    .buy-now-btn:hover:not(:disabled) { background: #333; transform: translateY(-1px); }
    .buy-now-btn:disabled { background: #bbb; cursor: not-allowed; }

    /* Botão secundário: carrinho em contorno */
    .add-btn {
      width: 48px;
      padding: 0.8rem;
      background: #fff;
      color: #111;
      border: 1.5px solid #111;
      border-radius: 50%;
      font-size: 1.1rem;
      cursor: pointer;
      transition: all 0.2s;
    }
    .add-btn:hover:not(:disabled) { background: #111; color: #fff; }
    .add-btn:disabled { border-color: #bbb; color: #bbb; cursor: not-allowed; }

    .loading, .empty { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
    .success-msg { color: #27ae60; font-size: 0.85rem; margin-top: 0.6rem; text-align: center; font-weight: 600; }
    .error-msg { color: #e74c3c; font-size: 0.85rem; margin-top: 0.6rem; text-align: center; font-weight: 600; }
  `]
})
export class ProductsComponent implements OnInit {
  products: ProductUI[] = [];
  loading = true;
  
  // Variável local para controlar a exibição do botão Admin
  isAdmin = false;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Verifica se é admin e salva na variável local
    this.isAdmin = this.authService.isAdmin();
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getActiveProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produtos:', err);
        this.loading = false;
      }
    });
  }

  getLowestPrice(variants: any[]): number {
    if (!variants || variants.length === 0) return 0;
    return Math.min(...variants.map((v: any) => v.preco));
  }

  addToCart(product: ProductUI): void {
    if (!product.variantes || product.variantes.length === 0) {
      product.uiErrorMessage = 'Produto sem variantes disponíveis.';
      setTimeout(() => product.uiErrorMessage = '', 4000);
      return;
    }

    product.isAdding = true;
    product.uiSuccessMessage = '';
    product.uiErrorMessage = '';

    const primeiraVariante = product.variantes[0];
    const request: AddToCartRequest = {
      varianteId: primeiraVariante.id,
      quantidade: 1
    };

    this.cartService.addToCart(request).subscribe({
      next: () => {
        product.isAdding = false;
        product.uiSuccessMessage = `✅ "${product.nome}" adicionado!`;
        setTimeout(() => product.uiSuccessMessage = '', 3000);
      },
      error: (err) => {
        product.isAdding = false;
        if (err.status === 400) {
          product.uiErrorMessage = '❌ Estoque insuficiente.';
        } else {
          product.uiErrorMessage = '❌ Erro ao adicionar.';
        }
        setTimeout(() => product.uiErrorMessage = '', 4000);
        console.error('Erro ao adicionar ao carrinho:', err);
      }
    });
  }

  /**
   * Adiciona o produto ao carrinho e redireciona imediatamente para o checkout.
   */
  buyNow(product: ProductUI): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!product.variantes || product.variantes.length === 0) {
      product.uiErrorMessage = 'Produto sem variantes disponíveis.';
      setTimeout(() => product.uiErrorMessage = '', 4000);
      return;
    }

    product.isAdding = true;
    product.uiSuccessMessage = '';
    product.uiErrorMessage = '';

    const primeiraVariante = product.variantes[0];
    const request: AddToCartRequest = {
      varianteId: primeiraVariante.id,
      quantidade: 1
    };

    this.cartService.addToCart(request).subscribe({
      next: () => {
        product.isAdding = false;
        // Redireciona imediatamente para o carrinho
        this.router.navigate(['/carrinho']);
      },
      error: (err) => {
        product.isAdding = false;
        if (err.status === 401) {
          this.router.navigate(['/login']);
        } else if (err.status === 400) {
          product.uiErrorMessage = ' Estoque insuficiente.';
        } else {
          product.uiErrorMessage = '❌ Erro ao processar compra.';
        }
        setTimeout(() => product.uiErrorMessage = '', 4000);
        console.error('Erro ao comprar agora:', err);
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
