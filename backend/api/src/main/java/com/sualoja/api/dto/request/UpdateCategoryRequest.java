package com.sualoja.api.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,
    
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    String descricao
) {}