package com.sualoja.api.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;


public record ProductRequest(
    @NotBlank(message = "O nome é obrigatório")
    String nome,
    
    @NotBlank(message = "A descrição é obrigatória")
    String descricao,
    
    @NotNull(message = "A categoria é obrigatória")
    Long categoriaId,
    
    Boolean ativo,
    
    Boolean destaque,
    
    String imagemUrl,
    
    List<ProductVariantRequest> variantes
) {}