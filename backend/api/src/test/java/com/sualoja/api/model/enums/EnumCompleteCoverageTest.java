package com.sualoja.api.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumCompleteCoverageTest {

    @Test
    @DisplayName("Cobrir todos os valores e métodos de UserRole")
    void cobrirUserRole() {
        UserRole[] roles = UserRole.values();
        assertTrue(roles.length >= 2);
        
        UserRole admin = UserRole.valueOf("ADMINISTRADOR");
        assertEquals(UserRole.ADMINISTRADOR, admin);
        
        UserRole cliente = UserRole.valueOf("CLIENTE");
        assertEquals(UserRole.CLIENTE, cliente);
        
        assertNotNull(admin.toString());
        assertNotNull(cliente.toString());
    }

    @Test
    @DisplayName("Cobrir todos os valores e métodos de OrderStatus")
    void cobrirOrderStatus() {
        OrderStatus[] statuses = OrderStatus.values();
        assertTrue(statuses.length >= 1);
        
        OrderStatus pendente = OrderStatus.valueOf("PENDENTE");
        assertEquals(OrderStatus.PENDENTE, pendente);
        assertNotNull(pendente.toString());
    }

    @Test
    @DisplayName("Cobrir todos os valores e métodos de CartStatus")
    void cobrirCartStatus() {
        CartStatus[] statuses = CartStatus.values();
        assertTrue(statuses.length >= 1);
        
        CartStatus status = CartStatus.values()[0];
        assertNotNull(status);
        assertNotNull(status.toString());
    }
}