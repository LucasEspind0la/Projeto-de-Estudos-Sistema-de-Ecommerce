package com.sualoja.api.service;

import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.*;
import com.sualoja.api.model.enums.OrderStatus;
import com.sualoja.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private OrderService orderService;

    private User usuario;
    private Cart carrinho;
    private ProductVariant variante;
    private CartItem itemCarrinho;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setId(1L);

        variante = new ProductVariant();
        variante.setId(1L);
        variante.setPreco(BigDecimal.valueOf(100.00));
        variante.setEstoque(10);
        
        Product produto = new Product();
        produto.setNome("Teste");
        variante.setProduto(produto);

        itemCarrinho = new CartItem();
        itemCarrinho.setVarianteProduto(variante);
        itemCarrinho.setQuantidade(2);

        carrinho = new Cart();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>(List.of(itemCarrinho)));
    }

    @Test
    @DisplayName("Deve lançar exceção se o carrinho estiver vazio")
    void deveLancarExcecaoSeCarrinhoVazio() {
        carrinho.getItens().clear();
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderService.finalizarPedido(1L);
        });
        assertTrue(ex.getMessage().contains("carrinho está vazio"));
    }

    @Test
    @DisplayName("Deve lançar exceção se o estoque for insuficiente")
    void deveLancarExcecaoSeEstoqueInsuficiente() {
        variante.setEstoque(1); // Estoque menor que a quantidade do carrinho (2)
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderService.finalizarPedido(1L);
        });
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pedido inexistente")
    void deveLancarExcecaoAoBuscarPedidoInexistente() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.buscarPedidoPorId(99L);
        });
        assertTrue(ex.getMessage().contains("Pedido não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status de pedido inexistente")
    void deveLancarExcecaoAoAtualizarStatusInexistente() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.atualizarStatusPedido(99L, OrderStatus.PAGO);
        });
        assertTrue(ex.getMessage().contains("Pedido não encontrado"));
    }
}