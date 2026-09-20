package com.sualoja.api.controller;

import com.sualoja.api.dto.request.CreateProductRequest;
import com.sualoja.api.dto.request.UpdateProductRequest;
import com.sualoja.api.dto.response.ProductResponse;
import com.sualoja.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// Controlador REST para CRUD de Produtos (base: /api/produtos)
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos") // <-- AQUI É O LUGAR CERTO
public class ProductController {

    
    private final ProductService productService;
    
    
    // POST /  Cria novo produto (valida payload). Retorna 201 Created.
    
       @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid CreateProductRequest requisicao) {
        // Chama o seu service para criar o produto
        ProductResponse produtoCriado = productService.criar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    // GET / Lista todos os produtos. Retorna 200 OK.
    @GetMapping
    public ResponseEntity<List<ProductResponse>> buscarTodos() {
        return ResponseEntity.ok(productService.buscarTodos());
    }

    // GET /ativos Lista apenas produtos com status ativo. Retorna 200 OK.
    @GetMapping("/ativos")
    public ResponseEntity<List<ProductResponse>> buscarAtivos() {
        return ResponseEntity.ok(productService.buscarAtivos());
    }

    // GET /{id}  Busca produto específico por ID. Retorna 200 OK.
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.buscarPorId(id));
    }

    // PUT /{id} Atualiza produto existente (valida payload). Retorna 200 OK.
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> atualizar(
        @PathVariable Long id,
        @RequestBody @Valid UpdateProductRequest requisicao
    ) {
        return ResponseEntity.ok(productService.atualizar(id, requisicao));
    }

    // PATCH /{id}/alternar-ativo -> Alterna o status de ativo/inativo do produto. Retorna 200 OK.
    @PatchMapping("/{id}/alternar-ativo")
    public ResponseEntity<ProductResponse> alternarStatusAtivo(@PathVariable Long id) {
        return ResponseEntity.ok(productService.alternarStatusAtivo(id));
    }

    // DELETE /{id}  Remove produto por ID. Retorna 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        productService.deletar(id);
        return ResponseEntity.noContent().build();
    }

        // PUT /{id}/ imagem! Atualiza a imagem do produto, se retornar 200 tudo ok!
        @PutMapping("/{id}/imagem")
    public ResponseEntity<ProductResponse> atualizarImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile arquivo) {
        
        ProductResponse produtoAtualizado = productService.atualizarImagem(id, arquivo);
        return ResponseEntity.ok(produtoAtualizado);
    }
}