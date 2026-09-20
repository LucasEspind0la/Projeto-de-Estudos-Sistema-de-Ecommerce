package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    Long usuarioId,
    String emailUsuario,
    String status,
    BigDecimal valorTotal,
    List<OrderItemResponse> itens,
    LocalDateTime criadoEm
) {
    public static OrderResponse deEntidade(Order pedido) {
        List<OrderItemResponse> itensResponse = pedido.getItens()
            .stream()
            .map(OrderItemResponse::deEntidade)
            .toList();
            
        return new OrderResponse(
            pedido.getId(),
            pedido.getUsuario().getId(),
            pedido.getUsuario().getEmail(),
            pedido.getStatus().name(),
            pedido.getValorTotal(),
            itensResponse,
            pedido.getCriadoEm()
        );
    }
}