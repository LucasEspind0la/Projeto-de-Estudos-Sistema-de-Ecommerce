package com.sualoja.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductVariantRequest(
    @NotBlank(message = "A cor é obrigatória")
    String cor,
    
    @NotBlank(message = "O tamanho é obrigatório")
    String tamanho,
    
    @NotBlank(message = "O SKU é obrigatório")
    String sku,
    
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    Double preco,
    
    @NotNull(message = "O estoque é obrigatório")
    Integer estoque
) {}