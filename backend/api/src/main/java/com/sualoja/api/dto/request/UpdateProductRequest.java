package com.sualoja.api.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    String nome,
    
    String descricao,
    
    Long categoriaId,
    
    Boolean ativo,
    Boolean destaque
) {}