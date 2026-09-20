package com.sualoja.api.controller;

import com.sualoja.api.dto.request.CreateCategoryRequest;
import com.sualoja.api.dto.request.UpdateCategoryRequest;
import com.sualoja.api.dto.response.CategoryResponse;
import com.sualoja.api.service.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST para CRUD de Categorias (base: /api/categorias)
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
public class CategoryController {

    private final CategoryService categoryService;

    // POST / -> Cria nova categoria (valida payload). Retorna 201 Created.
    @PostMapping
    public ResponseEntity<CategoryResponse> criar(@RequestBody @Valid CreateCategoryRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.criar(requisicao));
    }

    // GET / -> Lista todas as categorias. Retorna 200 OK.
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> buscarTodos() {
        return ResponseEntity.ok(categoryService.buscarTodos());
    }

    // GET /{id} -> Busca categoria específica por ID. Retorna 200 OK.
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.buscarPorId(id));
    }

    // PUT /{id} -> Atualiza categoria existente (valida payload). Retorna 200 OK.
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> atualizar(
        @PathVariable Long id,
        @RequestBody @Valid UpdateCategoryRequest requisicao
    ) {
        return ResponseEntity.ok(categoryService.atualizar(id, requisicao));
    }

    // DELETE /{id} -> Remove categoria por ID. Retorna 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoryService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}