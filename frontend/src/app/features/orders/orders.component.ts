import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { OrderService, Order } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, RouterLink],
  template: `s
    <div class="orders-container">
      <header class="header">
        <h1>Meus Pedidos</h1>
        <div class="header-actions">
          <button class="secondary-btn" routerLink="/produtos">Continuar Comprando</button>
          <button class="logout-btn" (click)="logout()">Sair</button>
        </div>
      </header>

      <div *ngIf="loading" class="loading">Carregando pedidos...</div>

      <div *ngIf="!loading && orders.length === 0" class="empty">
        <p>Você ainda não realizou nenhum pedido.</p>
        <button class="secondary-btn" routerLink="/produtos">Ir para a Loja</button>
      </div>

      <div *ngIf="!loading && orders.length > 0" class="orders-list">
        <div *ngFor="let order of orders" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <h3>Pedido #{{ order.id }}</h3>
              <p class="order-date">{{ order.criadoEm | date:'dd/MM/yyyy HH:mm' }}</p>
            </div>
            <div class="order-status">
              <span class="status-badge" [ngClass]="getStatusClass(order.status)">
                {{ order.status }}
              </span>
            </div>
          </div>

          <div class="order-items">
            <div *ngFor="let item of order.itens" class="order-item">
              <div class="item-details">
                <strong>{{ item.nomeProduto }}</strong>
                <span class="variant">{{ item.cor }} - Tam: {{ item.tamanho }}</span>
              </div>
              <div class="item-pricing">
                <span>{{ item.quantidade }}x {{ item.precoUnitario | currency:'BRL':'symbol':'1.2-2' }}</span>
                <span class="subtotal">{{ item.subtotal | currency:'BRL':'symbol':'1.2-2' }}</span>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="footer-left">
              <span class="total-label">Total:</span>
              <span class="total-value">{{ order.valorTotal | currency:'BRL':'symbol':'1.2-2' }}</span>
            </div>
            <button class="btn-details" routerLink="/pedidos/{{ order.id }}">Ver Detalhes</button>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .orders-container { padding: 2rem; max-width: 1000px; margin: 0 auto; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; background: #fff; min-height: 100vh; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #f0f0f0; }
    .header h1 { color: #111; margin: 0; font-size: 1.6rem; font-weight: 700; letter-spacing: -0.5px; }
    .header-actions { display: flex; gap: 0.75rem; }
    .secondary-btn { padding: 0.5rem 1rem; background: #fff; color: #111; border: 1px solid #ddd; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: border-color 0.2s; }
    .secondary-btn:hover { border-color: #111; }
    .logout-btn { padding: 0.5rem 1rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-weight: 600; font-size: 0.85rem; transition: all 0.2s; }
    .logout-btn:hover { background: #e74c3c; color: #fff; }
    .loading, .empty { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
    .empty p { margin-bottom: 1.5rem; color: #888; }
    .orders-list { display: flex; flex-direction: column; gap: 1.5rem; }
    .order-card { background: white; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); overflow: hidden; transition: box-shadow 0.2s; }
    .order-card:hover { box-shadow: 0 8px 25px rgba(0,0,0,0.1); }
    .order-header { display: flex; justify-content: space-between; align-items: center; padding: 1.25rem 1.5rem; background: #fafafa; border-bottom: 1px solid #f0f0f0; }
    .order-info h3 { margin: 0 0 0.25rem 0; color: #111; font-size: 1.05rem; font-weight: 700; }
    .order-date { margin: 0; color: #888; font-size: 0.85rem; }
    .status-badge { padding: 0.4rem 0.9rem; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
    .status-pendente { background: #fff3cd; color: #856404; }
    .status-pago { background: #cce5ff; color: #004085; }
    .status-enviado { background: #e2d9f3; color: #5a3d8a; }
    .status-entregue { background: #d4edda; color: #155724; }
    .status-cancelado { background: #f8d7da; color: #721c24; }
    .order-items { padding: 0.5rem 1.5rem; }
    .order-item { display: flex; justify-content: space-between; align-items: center; padding: 1rem 0; border-bottom: 1px solid #f5f5f5; }
    .order-item:last-child { border-bottom: none; }
    .item-details { display: flex; flex-direction: column; }
    .item-details strong { color: #111; font-size: 0.95rem; margin-bottom: 0.25rem; }
    .variant { color: #888; font-size: 0.85rem; }
    .item-pricing { display: flex; gap: 1.5rem; align-items: center; text-align: right; }
    .item-pricing span:first-child { color: #888; font-size: 0.85rem; }
    .subtotal { color: #111; font-weight: 700; min-width: 100px; }
    .order-footer { display: flex; justify-content: space-between; align-items: center; padding: 1.25rem 1.5rem; background: #fafafa; border-top: 1px solid #f0f0f0; }
    .footer-left { display: flex; align-items: center; gap: 0.75rem; }
    .total-label { color: #888; font-size: 0.9rem; }
    .total-value { color: #111; font-size: 1.3rem; font-weight: 700; }
    .btn-details { padding: 0.5rem 1.2rem; background: #111; color: white; border: none; border-radius: 20px; cursor: pointer; font-size: 0.85rem; font-weight: 600; transition: background 0.2s; }
    .btn-details:hover { background: #333; }
  `]
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  loading = true;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar pedidos:', err);
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    // Mapeamento exato com o Enum OrderStatus do seu Backend Java
    const statusMap: { [key: string]: string } = {
      'PENDENTE': 'status-pendente',
      'PAGO': 'status-pago',
      'ENVIADO': 'status-enviado',
      'ENTREGUE': 'status-entregue',
      'CANCELADO': 'status-cancelado'
    };
    return statusMap[status] || 'status-pendente';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}