package com.sualoja.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductVariantRequest(
    @NotBlank(message = "A cor é obrigatória")
    @Size(max = 50, message = "A cor deve ter no máximo 50 caracteres")
    String cor,
    
    @NotBlank(message = "O tamanho é obrigatório")
    @Size(max = 20, message = "O tamanho deve ter no máximo 20 caracteres")
    String tamanho,
    
    @NotBlank(message = "O SKU é obrigatório")
    @Size(max = 100, message = "O SKU deve ter no máximo 100 caracteres")
    String sku,
    
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    BigDecimal preco,
    
    @NotNull(message = "O estoque é obrigatório")
    @PositiveOrZero(message = "O estoque não pode ser negativo")
    Integer estoque
) {}