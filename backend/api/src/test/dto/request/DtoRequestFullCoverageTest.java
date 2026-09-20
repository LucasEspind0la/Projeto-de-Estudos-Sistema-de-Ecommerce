package com.sualoja.api.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoRequestFullCoverageTest {

    @Test
    @DisplayName("Cobrir ProductRequest - getters, equals, hashCode, toString")
    void cobrirProductRequest() {
        ProductVariantRequest variante = new ProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        ProductRequest request1 = new ProductRequest("Produto", "Desc", 1L, true, false, "url.jpg", List.of(variante));
        ProductRequest request2 = new ProductRequest("Produto", "Desc", 1L, true, false, "url.jpg", List.of(variante));
        
        assertEquals("Produto", request1.nome());
        assertEquals("Desc", request1.descricao());
        assertEquals(1L, request1.categoriaId());
        assertTrue(request1.ativo());
        assertFalse(request1.destaque());
        assertEquals("url.jpg", request1.imagemUrl());
        assertEquals(1, request1.variantes().size());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir ProductRequest com campos nulos")
    void cobrirProductRequestCamposNulos() {
        ProductRequest request = new ProductRequest("Produto", null, 1L, null, null, null, null);
        
        assertEquals("Produto", request.nome());
        assertNull(request.descricao());
        assertNull(request.ativo());
        assertNull(request.destaque());
        assertNull(request.imagemUrl());
        assertNull(request.variantes());
        assertNotNull(request.toString());
    }

    @Test
    @DisplayName("Cobrir ProductVariantRequest - getters, equals, hashCode, toString")
    void cobrirProductVariantRequest() {
        ProductVariantRequest request1 = new ProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        ProductVariantRequest request2 = new ProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        
        assertEquals("Preto", request1.cor());
        assertEquals("G", request1.tamanho());
        assertEquals("SKU-001", request1.sku());
        assertEquals(100.0, request1.preco());
        assertEquals(10, request1.estoque());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir CheckoutRequest - equals, hashCode, toString")
    void cobrirCheckoutRequest() {
        CheckoutRequest request1 = new CheckoutRequest();
        CheckoutRequest request2 = new CheckoutRequest();
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir LoginRequest - getters, equals, hashCode, toString")
    void cobrirLoginRequest() {
        LoginRequest request1 = new LoginRequest("user@test.com", "123456");
        LoginRequest request2 = new LoginRequest("user@test.com", "123456");
        
        assertEquals("user@test.com", request1.email());
        assertEquals("123456", request1.senha());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir CadastroRequest - getters, equals, hashCode, toString")
    void cobrirCadastroRequest() {
        CadastroRequest request1 = new CadastroRequest("Nome", "user@test.com", "123456");
        CadastroRequest request2 = new CadastroRequest("Nome", "user@test.com", "123456");
        
        assertEquals("Nome", request1.nome());
        assertEquals("user@test.com", request1.email());
        assertEquals("123456", request1.senha());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir CreateCategoryRequest - getters, equals, hashCode, toString")
    void cobrirCreateCategoryRequest() {
        CreateCategoryRequest request1 = new CreateCategoryRequest("Categoria", "Desc");
        CreateCategoryRequest request2 = new CreateCategoryRequest("Categoria", "Desc");
        
        assertEquals("Categoria", request1.nome());
        assertEquals("Desc", request1.descricao());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir UpdateCategoryRequest - getters, equals, hashCode, toString")
    void cobrirUpdateCategoryRequest() {
        UpdateCategoryRequest request1 = new UpdateCategoryRequest("Nova Categoria", "Nova Desc");
        UpdateCategoryRequest request2 = new UpdateCategoryRequest("Nova Categoria", "Nova Desc");
        
        assertEquals("Nova Categoria", request1.nome());
        assertEquals("Nova Desc", request1.descricao());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir CreateProductRequest - getters, equals, hashCode, toString")
    void cobrirCreateProductRequest() {
        CreateProductRequest request1 = new CreateProductRequest("Produto", "Desc", 1L, true, false, null);
        CreateProductRequest request2 = new CreateProductRequest("Produto", "Desc", 1L, true, false, null);
        
        assertEquals("Produto", request1.nome());
        assertEquals("Desc", request1.descricao());
        assertEquals(1L, request1.categoriaId());
        assertTrue(request1.ativo());
        assertFalse(request1.destaque());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir UpdateProductRequest - getters, equals, hashCode, toString")
    void cobrirUpdateProductRequest() {
        UpdateProductRequest request1 = new UpdateProductRequest("Produto", "Desc", 1L, true, false);
        UpdateProductRequest request2 = new UpdateProductRequest("Produto", "Desc", 1L, true, false);
        
        assertEquals("Produto", request1.nome());
        assertEquals("Desc", request1.descricao());
        assertEquals(1L, request1.categoriaId());
        assertTrue(request1.ativo());
        assertFalse(request1.destaque());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir CreateProductVariantRequest - getters, equals, hashCode, toString")
    void cobrirCreateProductVariantRequest() {
        CreateProductVariantRequest request1 = new CreateProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        CreateProductVariantRequest request2 = new CreateProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        
        assertEquals("Preto", request1.cor());
        assertEquals("G", request1.tamanho());
        assertEquals("SKU-001", request1.sku());
        assertEquals(100.0, request1.preco());
        assertEquals(10, request1.estoque());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir UpdateProductVariantRequest - getters, equals, hashCode, toString")
    void cobrirUpdateProductVariantRequest() {
        UpdateProductVariantRequest request1 = new UpdateProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        UpdateProductVariantRequest request2 = new UpdateProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        
        assertEquals("Preto", request1.cor());
        assertEquals("G", request1.tamanho());
        assertEquals("SKU-001", request1.sku());
        assertEquals(100.0, request1.preco());
        assertEquals(10, request1.estoque());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir AddToCartRequest - getters, equals, hashCode, toString")
    void cobrirAddToCartRequest() {
        AddToCartRequest request1 = new AddToCartRequest(1L, 2);
        AddToCartRequest request2 = new AddToCartRequest(1L, 2);
        
        assertEquals(1L, request1.varianteId());
        assertEquals(2, request1.quantidade());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    @DisplayName("Cobrir UpdateCartItemRequest - getters, equals, hashCode, toString")
    void cobrirUpdateCartItemRequest() {
        UpdateCartItemRequest request1 = new UpdateCartItemRequest(3);
        UpdateCartItemRequest request2 = new UpdateCartItemRequest(3);
        
        assertEquals(3, request1.quantidade());
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }
}