package com.sualoja.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateProductRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    String nome,
    
    String descricao,
    
    @NotNull(message = "A categoria é obrigatória")
    Long categoriaId,
    
    boolean ativo,
    boolean destaque,
    
    @Valid
    List<CreateProductVariantRequest> variantes
) {}