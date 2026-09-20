package com.sualoja.api.dto.response;

import com.sualoja.api.model.entity.Product;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
Long id,
String nome,
String descricao,
Long categoriaId,
String categoriaNome,
Boolean ativo,
Boolean destaque,
String imagemUrl,
List<ProductVariantResponse> variantes,
LocalDateTime criadoEm,
LocalDateTime atualizadoEm
) {
    private static final String PREFIXO_URL_IMAGEM = "/uploads/produtos/";

    private static String montarUrlImagem(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            return null;
        }
        if (nomeArquivo.startsWith("/uploads/") || nomeArquivo.startsWith("http")) {
            return nomeArquivo;
        }
        return PREFIXO_URL_IMAGEM + nomeArquivo;
    }

public static ProductResponse deEntidade(Product produto) {
List<ProductVariantResponse> variantesResponse = produto.getVariantes()
            .stream()
            .map(ProductVariantResponse::deEntidade)
            .toList();
return new ProductResponse(
produto.getId(),
produto.getNome(),
produto.getDescricao(),
produto.getCategoria().getId(),
produto.getCategoria().getNome(),
produto.getAtivo(),
produto.getDestaque(),
montarUrlImagem(produto.getImagemUrl()),
variantesResponse,
produto.getCriadoEm(),
produto.getAtualizadoEm()
        );
    }
}