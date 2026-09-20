package com.sualoja.api.controller;

import com.sualoja.api.dto.request.AddToCartRequest;
import com.sualoja.api.dto.request.UpdateCartItemRequest;
import com.sualoja.api.dto.response.CartResponse;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.service.CartService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
@RequiredArgsConstructor
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
public class CartController {

    private final CartService cartService;

    // Busca o carrinho do usuário autenticado
    @GetMapping
    public ResponseEntity<CartResponse> obterCarrinho(@AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(cartService.obterOuCriarCarrinho(usuario.getId()));
    }

    // Adiciona um item ao carrinho
    @PostMapping("/adicionar")
    public ResponseEntity<CartResponse> adicionarItem(
        @AuthenticationPrincipal User usuario,
        @RequestBody @Valid AddToCartRequest requisicao
    ) {
        return ResponseEntity.ok(cartService.adicionarItem(usuario.getId(), requisicao));
    }

    // Atualiza a quantidade de um item
    @PutMapping("/itens/{itemId}")
    public ResponseEntity<CartResponse> atualizarQuantidade(
        @AuthenticationPrincipal User usuario,
        @PathVariable Long itemId,
        @RequestBody @Valid UpdateCartItemRequest requisicao
    ) {
        return ResponseEntity.ok(cartService.atualizarQuantidadeItem(usuario.getId(), itemId, requisicao));
    }

    // Remove um item do carrinho
    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<CartResponse> removerItem(
        @AuthenticationPrincipal User usuario,
        @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(cartService.removerItem(usuario.getId(), itemId));
    }

    // Limpa todo o carrinho
    @DeleteMapping("/limpar")
    public ResponseEntity<Void> limparCarrinho(@AuthenticationPrincipal User usuario) {
        cartService.limparCarrinho(usuario.getId());
        return ResponseEntity.noContent().build();
    }
}