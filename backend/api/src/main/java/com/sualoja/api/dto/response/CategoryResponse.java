package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.Category;
import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String nome,
    String descricao,
    LocalDateTime criadoEm
) {
    public static CategoryResponse deEntidade(Category categoria) {
        return new CategoryResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao(),
            categoria.getCriadoEm()
        );
    }
}