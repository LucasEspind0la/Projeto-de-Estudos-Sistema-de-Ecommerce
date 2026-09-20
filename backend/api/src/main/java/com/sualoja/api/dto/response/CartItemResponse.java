package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.CartItem;
import java.math.BigDecimal;

public record CartItemResponse(
    Long id,
    Long varianteId,
    String nomeProduto,
    String cor,
    String tamanho,
    BigDecimal precoUnitario,
    Integer quantidade,
    BigDecimal subtotal
) {
    public static CartItemResponse deEntidade(CartItem item) {
        BigDecimal subtotal = item.getVarianteProduto().getPreco()
            .multiply(BigDecimal.valueOf(item.getQuantidade()));
            
        return new CartItemResponse(
            item.getId(),
            item.getVarianteProduto().getId(),
            item.getVarianteProduto().getProduto().getNome(),
            item.getVarianteProduto().getCor(),
            item.getVarianteProduto().getTamanho(),
            item.getVarianteProduto().getPreco(),
            item.getQuantidade(),
            subtotal
        );
    }
}