package com.sualoja.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Dados para adicionar um produto ao carrinho
public record AddToCartRequest(
    @NotNull(message = "A variação do produto é obrigatória")
    Long varianteId,
    
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    Integer quantidade
) {}