package com.sualoja.api.service;

import com.sualoja.api.dto.request.CreateProductVariantRequest;
import com.sualoja.api.dto.request.UpdateProductVariantRequest;
import com.sualoja.api.dto.response.ProductVariantResponse;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.Product;
import com.sualoja.api.model.entity.ProductVariant;
import com.sualoja.api.repository.ProductRepository;
import com.sualoja.api.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Serviço de regras de negócio para Variações de Produto. Depende de ProductVariantRepository e ProductRepository.
@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    // Cria variação vinculada ao produto. Valida SKU duplicado (lança IllegalArgumentException). Transacional.
    @Transactional
    public ProductVariantResponse criar(Long produtoId, CreateProductVariantRequest requisicao) {
        Product produto = productRepository.findById(produtoId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o id: " + produtoId));

        if (productVariantRepository.existsBySku(requisicao.sku())) {
            throw new IllegalArgumentException("Já existe uma variação com este SKU: " + requisicao.sku());
        }

        ProductVariant variante = ProductVariant.builder()
            .produto(produto)
            .cor(requisicao.cor())
            .tamanho(requisicao.tamanho())
            .sku(requisicao.sku())
            .preco(requisicao.preco())
            .estoque(requisicao.estoque())
            .build();

        return ProductVariantResponse.deEntidade(productVariantRepository.save(variante));
    }

    // Lista todas as variações de um produto específico. Somente leitura.
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> buscarPorProduto(Long produtoId) {
        return productVariantRepository.findByProdutoId(produtoId)
            .stream()
            .map(ProductVariantResponse::deEntidade)
            .toList();
    }

    // Busca variação por ID. Lança ResourceNotFoundException se não existir. Somente leitura.
    @Transactional(readOnly = true)
    public ProductVariantResponse buscarPorId(Long id) {
        ProductVariant variante = productVariantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Variação não encontrada com o id: " + id));
        return ProductVariantResponse.deEntidade(variante);
    }

    // Atualiza variação (parcial: só altera campos não nulos). Valida SKU duplicado se alterado. Transacional.
    @Transactional
    public ProductVariantResponse atualizar(Long id, UpdateProductVariantRequest requisicao) {
        ProductVariant variante = productVariantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Variação não encontrada com o id: " + id));

        if (requisicao.cor() != null) variante.setCor(requisicao.cor());
        if (requisicao.tamanho() != null) variante.setTamanho(requisicao.tamanho());
        
        if (requisicao.sku() != null && !requisicao.sku().equals(variante.getSku())) {
            if (productVariantRepository.existsBySku(requisicao.sku())) {
                throw new IllegalArgumentException("Já existe uma variação com este SKU: " + requisicao.sku());
            }
            variante.setSku(requisicao.sku());
        }

        if (requisicao.preco() != null) variante.setPreco(requisicao.preco());
        if (requisicao.estoque() != null) variante.setEstoque(requisicao.estoque());

        return ProductVariantResponse.deEntidade(productVariantRepository.save(variante));
    }

    // Remove variação por ID. Lança ResourceNotFoundException se não existir. Transacional.
    @Transactional
    public void deletar(Long id) {
        if (!productVariantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Variação não encontrada com o id: " + id);
        }
        productVariantRepository.deleteById(id);
    }
}