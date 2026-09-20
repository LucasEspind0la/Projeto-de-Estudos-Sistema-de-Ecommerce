package com.sualoja.api.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProductVariantRequest(
    @Size(max = 50, message = "A cor deve ter no máximo 50 caracteres")
    String cor,
    
    @Size(max = 20, message = "O tamanho deve ter no máximo 20 caracteres")
    String tamanho,
    
    @Size(max = 100, message = "O SKU deve ter no máximo 100 caracteres")
    String sku,
    
    @Positive(message = "O preço deve ser maior que zero")
    BigDecimal preco,
    
    @PositiveOrZero(message = "O estoque não pode ser negativo")
    Integer estoque
) {}