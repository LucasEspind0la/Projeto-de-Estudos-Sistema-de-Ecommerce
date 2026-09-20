package com.sualoja.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Dados para atualizar a quantidade de um item no carrinho
public record UpdateCartItemRequest(
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    Integer quantidade
) {}