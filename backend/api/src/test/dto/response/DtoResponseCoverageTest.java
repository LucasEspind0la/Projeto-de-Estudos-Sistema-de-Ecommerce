package com.sualoja.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DtoResponseCoverageTest {

    @Test
    @DisplayName("Cobrir construtores e métodos de todos os DTOs de Response")
    void cobrirTodosDtosDeResponse() {
        // AuthResponse
        AuthResponse auth = new AuthResponse("token", "user@test.com", "CLIENTE");
        assertNotNull(auth.token());

        // CategoryResponse
        CategoryResponse cat = new CategoryResponse(1L, "Nome", "Desc");
        assertNotNull(cat.nome());

        // ProductResponse
        ProductResponse prod = new ProductResponse(1L, "Nome", "Desc", true, false, cat, List.of());
        assertNotNull(prod.nome());

        // ProductVariantResponse
        ProductVariantResponse variant = new ProductVariantResponse(1L, "Cor", "Tam", "SKU", BigDecimal.TEN, 5, prod);
        assertNotNull(variant.cor());

        // CartItemResponse
        CartItemResponse cartItem = new CartItemResponse(1L, 1L, "Prod", "Var", "img", BigDecimal.TEN, 1, BigDecimal.TEN);
        assertNotNull(cartItem.nomeProduto());

        // CartResponse
        CartResponse cart = new CartResponse(1L, 1L, List.of(cartItem), BigDecimal.TEN, 1);
        assertNotNull(cart.total());

        // OrderItemResponse
        OrderItemResponse orderItem = new OrderItemResponse(1L, variant, 1, BigDecimal.TEN, BigDecimal.TEN);
        assertNotNull(orderItem.quantidade());

        // OrderResponse
        OrderResponse order = new OrderResponse(1L, 1L, "PENDENTE", "End", BigDecimal.TEN, List.of(orderItem), LocalDateTime.now());
        assertNotNull(order.status());

        // DashboardResponse
        DashboardResponse dash = new DashboardResponse(BigDecimal.TEN, 1L, 0L, List.of(order));
        assertNotNull(dash.totalPedidos());
    }
}