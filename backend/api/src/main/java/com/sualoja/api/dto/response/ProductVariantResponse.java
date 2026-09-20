package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.ProductVariant;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductVariantResponse(
    Long id,
    String cor,
    String tamanho,
    String sku,
    BigDecimal preco,
    Integer estoque,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
    public static ProductVariantResponse deEntidade(ProductVariant variante) {
        return new ProductVariantResponse(
            variante.getId(),
            variante.getCor(),
            variante.getTamanho(),
            variante.getSku(),
            variante.getPreco(),
            variante.getEstoque(),
            variante.getCriadoEm(),
            variante.getAtualizadoEm()
        );
    }
}