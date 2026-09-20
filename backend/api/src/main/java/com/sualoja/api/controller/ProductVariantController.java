package com.sualoja.api.controller;

import com.sualoja.api.dto.request.CreateProductVariantRequest;
import com.sualoja.api.dto.request.UpdateProductVariantRequest;
import com.sualoja.api.dto.response.ProductVariantResponse;
import com.sualoja.api.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST para CRUD de Variações de Produto (base: /api/produtos/{produtoId}/variacoes)
@RestController
@RequestMapping("/api/produtos/{produtoId}/variacoes")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    // POST / -> Cria nova variação para o produto informado. Retorna 201 Created.
    @PostMapping
    public ResponseEntity<ProductVariantResponse> criar(
        @PathVariable Long produtoId,
        @RequestBody @Valid CreateProductVariantRequest requisicao
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productVariantService.criar(produtoId, requisicao));
    }

    // GET / -> Lista todas as variações de um produto específico. Retorna 200 OK.
    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> buscarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(productVariantService.buscarPorProduto(produtoId));
    }

    // GET /{id} -> Busca variação específica por ID. Retorna 200 OK.
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantResponse> buscarPorId(
        @PathVariable Long produtoId,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(productVariantService.buscarPorId(id));
    }

    // PUT /{id} -> Atualiza variação existente (valida payload). Retorna 200 OK.
    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantResponse> atualizar(
        @PathVariable Long produtoId,
        @PathVariable Long id,
        @RequestBody @Valid UpdateProductVariantRequest requisicao
    ) {
        return ResponseEntity.ok(productVariantService.atualizar(id, requisicao));
    }

    // DELETE /{id} -> Remove variação por ID. Retorna 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
        @PathVariable Long produtoId,
        @PathVariable Long id
    ) {
        productVariantService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}