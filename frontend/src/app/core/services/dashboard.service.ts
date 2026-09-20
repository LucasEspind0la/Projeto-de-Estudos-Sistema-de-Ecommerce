import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from './order.service';

export interface DashboardMetrics {
  faturamentoTotal: number;
  totalPedidos: number;
  produtosEstoqueBaixo: number;
  ultimosPedidos: Order[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  getMetrics(): Observable<DashboardMetrics> {
    return this.http.get<DashboardMetrics>('/api/admin/dashboard');
  }
}
