package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.OrderItem;
import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long varianteId,
    String nomeProduto,
    String cor,
    String tamanho,
    BigDecimal precoUnitario,
    Integer quantidade,
    BigDecimal subtotal
) {
    public static OrderItemResponse deEntidade(OrderItem item) {
        BigDecimal subtotal = item.getPrecoUnitario()
            .multiply(BigDecimal.valueOf(item.getQuantidade()));
            
        return new OrderItemResponse(
            item.getId(),
            item.getVarianteProduto().getId(),
            item.getVarianteProduto().getProduto().getNome(),
            item.getVarianteProduto().getCor(),
            item.getVarianteProduto().getTamanho(),
            item.getPrecoUnitario(),
            item.getQuantidade(),
            subtotal
        );
    }
}