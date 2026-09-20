package com.sualoja.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
    BigDecimal faturamentoTotal,
    long totalPedidos,
    long produtosEstoqueBaixo,
    List<OrderResponse> ultimosPedidos
) {}
