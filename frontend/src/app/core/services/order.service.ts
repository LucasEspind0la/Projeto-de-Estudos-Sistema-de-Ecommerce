import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderItem {
  id: number;
  varianteId: number;
  nomeProduto: string;
  cor: string;
  tamanho: string;
  precoUnitario: number;
  quantidade: number;
  subtotal: number;
}

export interface Order {
  id: number;
  usuarioId: number;
  emailUsuario: string;
  status: string;
  valorTotal: number;
  itens: OrderItem[];
  criadoEm: string;
}

export type OrderResponse = Order;

@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(private http: HttpClient) {}

  checkout(): Observable<Order> {
    return this.http.post<Order>('/api/pedidos/finalizar', {});
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>('/api/pedidos/meus-pedidos');
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`/api/pedidos/${id}`);
  }

  // MÉTODO ADICIONADO PARA O ADMIN
  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>('/api/pedidos');
  }

  updateOrderStatus(id: number, status: string): Observable<Order> {
    return this.http.patch<Order>(`/api/pedidos/${id}/status?status=${status}`, {});
  }
}
