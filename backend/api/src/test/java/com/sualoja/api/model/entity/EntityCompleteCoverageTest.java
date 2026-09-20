package com.sualoja.api.model.entity;

import com.sualoja.api.model.enums.OrderStatus;
import com.sualoja.api.model.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class EntityCompleteCoverageTest {

    @Test
    @DisplayName("Cobrir User - getters, setters e toString")
    void cobrirUser() {
        User user = new User();
        user.setId(1L);
        user.setNome("Teste");
        user.setEmail("teste@test.com");
        user.setSenha("123");
        user.setPapel(UserRole.CLIENTE);
        
        assertEquals(1L, user.getId());
        assertEquals("Teste", user.getNome());
        assertEquals("teste@test.com", user.getEmail());
        assertEquals("123", user.getSenha());
        assertEquals(UserRole.CLIENTE, user.getPapel());
        assertNotNull(user.toString());
    }

    @Test
    @DisplayName("Cobrir Product - getters, setters e toString")
    void cobrirProduct() {
        Product prod = new Product();
        prod.setId(1L);
        prod.setNome("Produto");
        prod.setDescricao("Desc");
        prod.setAtivo(true);
        prod.setDestaque(false);
        
        assertEquals(1L, prod.getId());
        assertEquals("Produto", prod.getNome());
        assertEquals("Desc", prod.getDescricao());
        assertTrue(prod.getAtivo());
        assertFalse(prod.getDestaque());
        assertNotNull(prod.toString());
    }

    @Test
    @DisplayName("Cobrir Category - getters, setters e toString")
    void cobrirCategory() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setNome("Categoria");
        cat.setDescricao("Desc");
        
        assertEquals(1L, cat.getId());
        assertEquals("Categoria", cat.getNome());
        assertEquals("Desc", cat.getDescricao());
        assertNotNull(cat.toString());
    }

    @Test
    @DisplayName("Cobrir ProductVariant - getters, setters e toString")
    void cobrirProductVariant() {
        ProductVariant variant = new ProductVariant();
        variant.setId(1L);
        variant.setCor("Preto");
        variant.setTamanho("G");
        variant.setSku("SKU-001");
        variant.setPreco(BigDecimal.valueOf(100.00));
        variant.setEstoque(10);
        
        assertEquals(1L, variant.getId());
        assertEquals("Preto", variant.getCor());
        assertEquals("G", variant.getTamanho());
        assertEquals("SKU-001", variant.getSku());
        assertEquals(BigDecimal.valueOf(100.00), variant.getPreco());
        assertEquals(10, variant.getEstoque());
        assertNotNull(variant.toString());
    }

    @Test
    @DisplayName("Cobrir Cart - getters, setters e toString")
    void cobrirCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUsuario(new User());
        cart.setItens(new ArrayList<>());
        
        assertEquals(1L, cart.getId());
        assertNotNull(cart.getUsuario());
        assertNotNull(cart.getItens());
        assertNotNull(cart.toString());
    }

    @Test
    @DisplayName("Cobrir CartItem - getters, setters e toString")
    void cobrirCartItem() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setQuantidade(2);
        item.setVarianteProduto(new ProductVariant());
        
        assertEquals(1L, item.getId());
        assertEquals(2, item.getQuantidade());
        assertNotNull(item.getVarianteProduto());
        assertNotNull(item.toString());
    }

    @Test
    @DisplayName("Cobrir Order - getters, setters e toString")
    void cobrirOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setUsuario(new User());
        order.setStatus(OrderStatus.PENDENTE);
        order.setValorTotal(BigDecimal.valueOf(500.00));
        order.setCriadoEm(LocalDateTime.now());
        order.setItens(new ArrayList<>());
        
        assertEquals(1L, order.getId());
        assertEquals(OrderStatus.PENDENTE, order.getStatus());
        assertEquals(BigDecimal.valueOf(500.00), order.getValorTotal());
        assertNotNull(order.getCriadoEm());
        assertNotNull(order.toString());
    }

    @Test
    @DisplayName("Cobrir OrderItem - getters, setters e toString")
    void cobrirOrderItem() {
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setQuantidade(2);
        item.setPrecoUnitario(BigDecimal.valueOf(100.00));
        item.setVarianteProduto(new ProductVariant());
        
        assertEquals(1L, item.getId());
        assertEquals(2, item.getQuantidade());
        assertEquals(BigDecimal.valueOf(100.00), item.getPrecoUnitario());
        assertNotNull(item.getVarianteProduto());
        assertNotNull(item.toString());
    }
}