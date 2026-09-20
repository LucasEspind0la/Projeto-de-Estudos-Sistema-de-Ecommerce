package com.sualoja.api.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoRequestFullCoverageTest {

    @Test
    @DisplayName("Cobrir ProductRequest")
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
    @DisplayName("Cobrir ProductVariantRequest")
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
    @DisplayName("Cobrir CheckoutRequest")
    void cobrirCheckoutRequest() {
        CheckoutRequest request1 = new CheckoutRequest();
        CheckoutRequest request2 = new CheckoutRequest();
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }
}
