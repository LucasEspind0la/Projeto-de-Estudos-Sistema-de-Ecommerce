import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { DashboardService, DashboardMetrics } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, RouterLink],
  template: `
    <div class="dashboard-container">
      <header class="header">
        <h1>Painel Administrativo</h1>
        <div class="header-actions">
          <button class="secondary-btn" routerLink="/admin/produtos">Gerenciar Produtos</button>
          <button class="secondary-btn" routerLink="/admin/categorias">Gerenciar Categorias</button>
          <button class="secondary-btn" routerLink="/admin/pedidos">📦 Gerenciar Pedidos</button>
          <button class="logout-btn" (click)="logout()">Sair</button>
        </div>
      </header>

      <div *ngIf="loading" class="loading">Carregando métricas...</div>

      <div *ngIf="!loading && metrics" class="content">
        <!-- Cards de Métricas -->
        <div class="cards-grid">
          <div class="metric-card revenue">
            <div class="icon">💰</div>
            <div class="info">
              <span class="label">Faturamento Total</span>
              <span class="value">{{ metrics.faturamentoTotal | currency:'BRL':'symbol':'1.2-2' }}</span>
            </div>
          </div>
          <div class="metric-card orders">
            <div class="icon">📦</div>
            <div class="info">
              <span class="label">Total de Pedidos</span>
              <span class="value">{{ metrics.totalPedidos }}</span>
            </div>
          </div>
          <div class="metric-card warning" [class.alert]="metrics.produtosEstoqueBaixo > 0">
            <div class="icon">⚠️</div>
            <div class="info">
              <span class="label">Estoque Baixo (< 5)</span>
              <span class="value">{{ metrics.produtosEstoqueBaixo }}</span>
            </div>
          </div>
        </div>

        <!-- Tabela de Últimos Pedidos -->
        <div class="recent-orders">
          <h2>Últimos 5 Pedidos</h2>
          <div *ngIf="metrics.ultimosPedidos.length === 0" class="empty">Nenhum pedido realizado ainda.</div>
          <table *ngIf="metrics.ultimosPedidos.length > 0" class="orders-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Data</th>
                <th>Cliente</th>
                <th>Status</th>
                <th class="text-right">Valor</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let order of metrics.ultimosPedidos">
                <td>#{{ order.id }}</td>
                <td>{{ order.criadoEm | date:'dd/MM/yyyy HH:mm' }}</td>
                <td>{{ order.emailUsuario }}</td>
                <td><span class="status-badge" [ngClass]="'status-' + order.status.toLowerCase()">{{ order.status }}</span></td>
                <td class="text-right">{{ order.valorTotal | currency:'BRL':'symbol':'1.2-2' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .dashboard-container { padding: 2rem; max-width: 1200px; margin: 0 auto; font-family: 'Helvetica Neue', 'Segoe UI', Arial, sans-serif; background: #fff; min-height: 100vh; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #f0f0f0; }
    .header h1 { color: #111; margin: 0; font-size: 1.6rem; font-weight: 700; letter-spacing: -0.5px; }
    .header-actions { display: flex; gap: 0.75rem; flex-wrap: wrap; }
    .secondary-btn { padding: 0.5rem 1rem; background: #fff; color: #111; border: 1px solid #ddd; border-radius: 20px; cursor: pointer; font-weight: 600; text-decoration: none; font-size: 0.85rem; transition: border-color 0.2s; }
    .secondary-btn:hover { border-color: #111; }
    .logout-btn { padding: 0.5rem 1rem; background: #fff; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 20px; cursor: pointer; font-weight: 600; font-size: 0.85rem; transition: all 0.2s; }
    .logout-btn:hover { background: #e74c3c; color: #fff; }
    .content { display: flex; flex-direction: column; gap: 2rem; }
    .cards-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem; }
    .metric-card { display: flex; align-items: center; gap: 1.5rem; background: white; padding: 1.75rem; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); transition: transform 0.2s, box-shadow 0.2s; }
    .metric-card:hover { transform: translateY(-4px); box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
    .metric-card.revenue { border-bottom: 4px solid #27ae60; }
    .metric-card.orders { border-bottom: 4px solid #111; }
    .metric-card.warning { border-bottom: 4px solid #f39c12; }
    .metric-card.warning.alert { border-bottom-color: #e74c3c; background: #fff5f5; }
    .icon { font-size: 2.2rem; }
    .info { display: flex; flex-direction: column; }
    .label { color: #888; font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
    .value { color: #111; font-size: 1.7rem; font-weight: 700; margin-top: 0.25rem; }
    .recent-orders { background: white; padding: 1.75rem; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }
    .recent-orders h2 { margin-top: 0; color: #111; font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; }
    .orders-table { width: 100%; border-collapse: collapse; }
    .orders-table th { text-align: left; padding: 0.75rem 1rem; background: #f6f6f6; color: #111; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .orders-table td { padding: 1rem; border-bottom: 1px solid #f5f5f5; color: #444; font-size: 0.9rem; }
    .text-right { text-align: right; font-weight: 700; }
    .status-badge { padding: 0.35rem 0.75rem; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
    .status-pendente { background: #fff3cd; color: #856404; }
    .status-pago { background: #cce5ff; color: #004085; }
    .status-enviado { background: #e2d9f3; color: #5a3d8a; }
    .status-entregue { background: #d4edda; color: #155724; }
    .status-cancelado { background: #f8d7da; color: #721c24; }
    .loading, .empty { text-align: center; padding: 3rem; color: #999; font-size: 1rem; }
  `]
})
export class AdminDashboardComponent implements OnInit {
  metrics: DashboardMetrics | null = null;
  loading = true;

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/login']);
      return;
    }
    this.dashboardService.getMetrics().subscribe({
      next: (data) => {
        this.metrics = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar dashboard:', err);
        this.loading = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}