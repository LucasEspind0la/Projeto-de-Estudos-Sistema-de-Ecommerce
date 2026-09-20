package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.Cart;
import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Long id,
    Long usuarioId,
    List<CartItemResponse> itens,
    BigDecimal valorTotal,
    Integer totalItens
) {
    public static CartResponse deEntidade(Cart carrinho) {
        List<CartItemResponse> itensResponse = carrinho.getItens()
            .stream()
            .map(CartItemResponse::deEntidade)
            .toList();
        
        // Soma o subtotal de cada item para calcular o total
        BigDecimal valorTotal = itensResponse.stream()
            .map(CartItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Soma a quantidade de todos os itens
        Integer totalItens = itensResponse.stream()
            .mapToInt(CartItemResponse::quantidade)
            .sum();
            
        return new CartResponse(
            carrinho.getId(),
            carrinho.getUsuario().getId(),
            itensResponse,
            valorTotal,
            totalItens
        );
    }
}