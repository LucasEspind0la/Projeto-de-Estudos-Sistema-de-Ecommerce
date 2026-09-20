package com.sualoja.api.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DtoRequestGapCoverageTest {

    @Test
    @DisplayName("Cobrir ProductVariantRequest")
    void cobrirProductVariantRequest() {
        ProductVariantRequest request = new ProductVariantRequest("Preto", "G", "SKU-123", 100.0, 10);
        assertEquals("Preto", request.cor());
        assertEquals("G", request.tamanho());
        assertEquals("SKU-123", request.sku());
        assertEquals(100.0, request.preco());
        assertEquals(10, request.estoque());
    }

    @Test
    @DisplayName("Cobrir ProductRequest")
    void cobrirProductRequest() {
        ProductVariantRequest variante = new ProductVariantRequest("Azul", "M", "SKU-456", 50.0, 5);
        ProductRequest request = new ProductRequest("Produto Teste", "Descrição", 1L, true, false, "url.jpg", List.of(variante));
        assertEquals("Produto Teste", request.nome());
        assertEquals("Descrição", request.descricao());
        assertEquals(1L, request.categoriaId());
        assertTrue(request.ativo());
        assertFalse(request.destaque());
        assertEquals("url.jpg", request.imagemUrl());
        assertEquals(1, request.variantes().size());
    }

    @Test
    @DisplayName("Cobrir CheckoutRequest")
    void cobrirCheckoutRequest() {
        CheckoutRequest request = new CheckoutRequest();
        assertNotNull(request);
    }
}