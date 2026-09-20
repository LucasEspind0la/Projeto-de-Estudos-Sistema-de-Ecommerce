import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { OrderService, OrderResponse } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, RouterLink],
  template: `
    <div class="details-container">
      <header class="header">
        <div>
          <button class="back-btn" routerLink="/pedidos">← Voltar para Meus Pedidos</button>
          <h1 *ngIf="order">Detalhes do Pedido #{{ order.id }}</h1>
          <h1 *ngIf="!order && !loading">Pedido não encontrado</h1>
        </div>
        <button class="logout-btn" (click)="logout()">Sair</button>
      </header>

      <div *ngIf="loading" class="loading">Carregando detalhes do pedido...</div>

      <div *ngIf="order" class="content">
        <!-- Resumo do Pedido -->
        <div class="summary-card">
          <div class="summary-item">
            <span class="label">Data da Compra</span>
            <span class="value">{{ order.criadoEm | date:'dd/MM/yyyy HH:mm' }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Status</span>
            <span class="value">
              <span class="status-badge" [ngClass]="getStatusClass(order.status)">
                {{ order.status }}
              </span>
            </span>
          </div>
          <div class="summary-item total">
            <span class="label">Valor Total</span>
            <span class="value">{{ order.valorTotal | currency:'BRL':'symbol':'1.2-2' }}</span>
          </div>
        </div>

        <!-- Lista de Itens -->
        <div class="items-card">
          <h2>Itens do Pedido</h2>
          <table class="items-table">
            <thead>
              <tr>
                <th>Produto</th>
                <th>Variação</th>
                <th>Qtd</th>
                <th>Preço Unit.</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let item of order.itens">
                <td class="product-name">{{ item.nomeProduto }}</td>
                <td>{{ item.cor }} - Tam: {{ item.tamanho }}</td>
                <td class="text-center">{{ item.quantidade }}</td>
                <td>{{ item.precoUnitario | currency:'BRL':'symbol':'1.2-2' }}</td>
                <td class="text-right">{{ item.subtotal | currency:'BRL':'symbol':'1.2-2' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .details-container { padding: 2rem; max-width: 900px; margin: 0 auto; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; background: #fff; min-height: 100vh; }
    .header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #f0f0f0; }
    .back-btn { background: none; border: none; color: #888; cursor: pointer; font-size: 0.85rem; font-weight: 600; padding: 0; margin-bottom: 0.5rem; transition: color 0.2s; }
    .back-btn:hover { color: #111; }
    .header h1 { color: #111; margin: 0; font-size: 1.5rem; font-weight: 700; letter-spacing: -0.5px; }
    .logout-btn { padding: 0.5rem 1rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-weight: 600; font-size: 0.85rem; transition: all 0.2s; }
    .logout-btn:hover { background: #e74c3c; color: #fff; }
    .content { display: flex; flex-direction: column; gap: 1.5rem; }
    .summary-card { display: flex; justify-content: space-between; background: white; padding: 1.5rem; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); flex-wrap: wrap; gap: 1rem; }
    .summary-item { display: flex; flex-direction: column; }
    .summary-item.total { text-align: right; }
    .label { font-size: 0.75rem; color: #888; margin-bottom: 0.35rem; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }
    .value { font-size: 1.05rem; font-weight: 600; color: #111; }
    .summary-item.total .value { font-size: 1.4rem; font-weight: 700; }
    .status-badge { padding: 0.35rem 0.75rem; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
    .status-pendente { background: #fff3cd; color: #856404; }
    .status-pago { background: #cce5ff; color: #004085; }
    .status-enviado { background: #e2d9f3; color: #5a3d8a; }
    .status-entregue { background: #d4edda; color: #155724; }
    .status-cancelado { background: #f8d7da; color: #721c24; }
    .items-card { background: white; padding: 1.5rem; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }
    .items-card h2 { margin-top: 0; color: #111; font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; }
    .items-table { width: 100%; border-collapse: collapse; }
    .items-table th { text-align: left; padding: 0.75rem; background: #f6f6f6; color: #111; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid #f0f0f0; }
    .items-table td { padding: 1rem 0.75rem; border-bottom: 1px solid #f5f5f5; color: #444; font-size: 0.9rem; }
    .product-name { font-weight: 600; color: #111; }
    .text-center { text-align: center; }
    .text-right { text-align: right; font-weight: 700; }
    .loading { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
  `]
})
export class OrderDetailsComponent implements OnInit {
  order: OrderResponse | null = null;
  loading = true;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.router.navigate(['/pedidos']);
      return;
    }

    const orderId = +idParam;
    this.orderService.getOrderById(orderId).subscribe({
      next: (data) => {
        this.order = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar pedido:', err);
        this.loading = false;
        // Se der erro (ex: 403 ou 404), redireciona para a lista
        this.router.navigate(['/pedidos']);
      }
    });
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}