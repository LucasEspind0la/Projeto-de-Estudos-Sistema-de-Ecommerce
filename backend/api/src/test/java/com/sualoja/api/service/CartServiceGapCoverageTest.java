package com.sualoja.api.service;

import com.sualoja.api.dto.request.AddToCartRequest;
import com.sualoja.api.dto.request.UpdateCartItemRequest;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.Cart;
import com.sualoja.api.model.entity.CartItem;
import com.sualoja.api.model.entity.Product;
import com.sualoja.api.model.entity.ProductVariant;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.repository.CartItemRepository;
import com.sualoja.api.repository.CartRepository;
import com.sualoja.api.repository.ProductVariantRepository;
import com.sualoja.api.repository.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartServiceGapCoverageTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private CartService cartService;

    private User usuario;
    private Cart carrinho;
    private ProductVariant variante;
    private CartItem item;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setId(1L);

        // CORREÇÃO: Criar um produto e associá-lo à variante para evitar NPE no CartResponse
        Product produto = new Product();
        produto.setId(1L);
        produto.setNome("Produto Teste");

        variante = new ProductVariant();
        variante.setId(1L);
        variante.setEstoque(10);
        variante.setPreco(BigDecimal.valueOf(100.00));
        variante.setProduto(produto); // <-- Associando o produto à variante

        item = new CartItem();
        item.setId(1L);
        item.setQuantidade(2);
        item.setVarianteProduto(variante);
        item.setCarrinho(new Cart());
        item.getCarrinho().setUsuario(usuario);

        carrinho = new Cart();
        carrinho.setId(1L);
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());
        carrinho.getItens().add(item);
    }

    @Test
    @DisplayName("Deve atualizar quantidade com sucesso")
    void deveAtualizarQuantidadeComSucesso() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));
        
        cartService.atualizarQuantidadeItem(1L, 1L, new UpdateCartItemRequest(5));
        assertEquals(5, item.getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar item de outro usuário")
    void deveLancarExcecaoItemDeOutroUsuario() {
        User outroUsuario = new User();
        outroUsuario.setId(2L);
        item.getCarrinho().setUsuario(outroUsuario);
        
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.atualizarQuantidadeItem(1L, 1L, new UpdateCartItemRequest(5));
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com estoque insuficiente")
    void deveLancarExcecaoEstoqueInsuficienteAtualizacao() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.atualizarQuantidadeItem(1L, 1L, new UpdateCartItemRequest(15)); // Estoque é 10
        });
    }

    @Test
    @DisplayName("Deve remover item do carrinho com sucesso")
    void deveRemoverItemComSucesso() {
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));
        when(cartRepository.save(any(Cart.class))).thenReturn(carrinho);

        cartService.removerItem(1L, 1L);
        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover item inexistente")
    void deveLancarExcecaoItemInexistente() {
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removerItem(1L, 99L);
        });
    }

    @Test
    @DisplayName("Deve limpar carrinho com sucesso")
    void deveLimparCarrinhoComSucesso() {
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));
        when(cartRepository.save(any(Cart.class))).thenReturn(carrinho);

        cartService.limparCarrinho(1L);
        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao limpar carrinho inexistente")
    void deveLancarExcecaoCarrinhoInexistente() {
        when(cartRepository.findByUsuarioId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.limparCarrinho(1L);
        });
    }
}