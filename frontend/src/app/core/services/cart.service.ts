import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AddToCartRequest {
  varianteId: number;
  quantidade: number;
}

export interface UpdateCartItemRequest {
  quantidade: number;
}

export interface CartItem {
  id: number;
  varianteId: number;
  nomeProduto: string;
  cor: string;
  tamanho: string;
  precoUnitario: number;
  quantidade: number;
  subtotal: number;
}

export interface CartResponse {
  id: number;
  usuarioId: number;
  itens: CartItem[];
  valorTotal: number;
  totalItens: number;
}

/**
 * Serviço responsável por gerenciar as operações do carrinho de compras.
 */
@Injectable({ providedIn: 'root' })
export class CartService {
  constructor(private http: HttpClient) {}

  /**
   * Busca o carrinho atual do usuário logado.
   */
  getCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>('/api/carrinho');
  }

  /**
   * Adiciona um item ao carrinho (ou incrementa se já existir).
   */
  addToCart(request: AddToCartRequest): Observable<CartResponse> {
    return this.http.post<CartResponse>('/api/carrinho/adicionar', request);
  }

  /**
   * Atualiza a quantidade de um item específico no carrinho.
   */
  updateItemQuantity(itemId: number, quantidade: number): Observable<CartResponse> {
    const payload: UpdateCartItemRequest = { quantidade };
    return this.http.put<CartResponse>(`/api/carrinho/itens/${itemId}`, payload);
  }

  /**
   * Remove um item específico do carrinho.
   */
  removeItem(itemId: number): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`/api/carrinho/itens/${itemId}`);
  }
}