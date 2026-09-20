import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, switchMap } from 'rxjs';

export interface CreateProductVariantRequest {
  cor: string;
  tamanho: string;
  sku: string;
  preco: number;
  estoque: number;
}

export interface CreateProductRequest {
  nome: string;
  descricao: string;
  categoriaId: number;
  ativo: boolean;
  destaque: boolean;
  variantes: CreateProductVariantRequest[];
}

export interface UpdateProductRequest {
  nome?: string;
  descricao?: string;
  categoriaId?: number;
  ativo?: boolean;
  destaque?: boolean;
}

export interface UpdateProductVariantRequest {
  cor?: string;
  tamanho?: string;
  sku?: string;
  preco?: number;
  estoque?: number;
}

export interface ProductVariant {
  id: number;
  cor: string;
  tamanho: string;
  sku: string;
  preco: number;
  estoque: number;
}

export interface Product {
  id: number;
  nome: string;
  descricao: string;
  categoriaId: number;
  categoriaNome: string;
  ativo: boolean;
  destaque: boolean;
  imagemUrl: string | null;
  variantes: ProductVariant[];
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  constructor(private http: HttpClient) {}

  getActiveProducts(): Observable<Product[]> {
    return this.http.get<Product[]>('/api/produtos/ativos');
  }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>('/api/produtos');
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`/api/produtos/${id}`);
  }

  createProductWithImage(payload: CreateProductRequest, file?: File): Observable<Product> {
    return this.http.post<Product>('/api/produtos', payload).pipe(
      switchMap((createdProduct) => {
        if (!file) return of(createdProduct);
        const formData = new FormData();
        formData.append('imagem', file, file.name);
        return this.http.put<Product>(`/api/produtos/${createdProduct.id}/imagem`, formData).pipe(
          switchMap(() => of(createdProduct))
        );
      })
    );
  }

  updateProduct(id: number, payload: UpdateProductRequest): Observable<Product> {
    return this.http.put<Product>(`/api/produtos/${id}`, payload);
  }

  updateVariant(produtoId: number, varianteId: number, payload: UpdateProductVariantRequest): Observable<any> {
    return this.http.put(`/api/produtos/${produtoId}/variacoes/${varianteId}`, payload);
  }

  uploadImage(produtoId: number, file: File): Observable<Product> {
    const formData = new FormData();
    formData.append('imagem', file, file.name);
    return this.http.put<Product>(`/api/produtos/${produtoId}/imagem`, formData);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`/api/produtos/${id}`);
  }

  toggleActive(id: number): Observable<Product> {
    return this.http.patch<Product>(`/api/produtos/${id}/alternar-ativo`, {});
  }
}