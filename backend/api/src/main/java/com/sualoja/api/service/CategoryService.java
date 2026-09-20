package com.sualoja.api.service;

import com.sualoja.api.dto.request.CreateCategoryRequest;
import com.sualoja.api.dto.request.UpdateCategoryRequest;
import com.sualoja.api.dto.response.CategoryResponse;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Serviço de regras de negócio para Categorias. Depende de CategoryRepository.
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Cria categoria. Valida nome duplicado (lança IllegalArgumentException). Transacional.
    @Transactional
    public CategoryResponse criar(CreateCategoryRequest requisicao) {
        if (categoryRepository.existsByNome(requisicao.nome())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        Category categoria = Category.builder()
            .nome(requisicao.nome())
            .descricao(requisicao.descricao())
            .build();

        return CategoryResponse.deEntidade(categoryRepository.save(categoria));
    }

    // Lista todas as categorias. Somente leitura.
    @Transactional(readOnly = true)
    public List<CategoryResponse> buscarTodos() {
        return categoryRepository.findAll()
            .stream()
            .map(CategoryResponse::deEntidade)
            .toList();
    }

    // Busca categoria por ID. Lança ResourceNotFoundException se não existir. Somente leitura.
    @Transactional(readOnly = true)
    public CategoryResponse buscarPorId(Long id) {
        Category categoria = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o id: " + id));
        return CategoryResponse.deEntidade(categoria);
    }

    // Atualiza categoria (parcial: só altera campos não nulos). Valida nome duplicado se alterado. Transacional.
    @Transactional
    public CategoryResponse atualizar(Long id, UpdateCategoryRequest requisicao) {
        Category categoria = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o id: " + id));

        if (requisicao.nome() != null && !requisicao.nome().equals(categoria.getNome())) {
            if (categoryRepository.existsByNome(requisicao.nome())) {
                throw new IllegalArgumentException("Já existe uma categoria com este nome");
            }
            categoria.setNome(requisicao.nome());
        }

        if (requisicao.descricao() != null) {
            categoria.setDescricao(requisicao.descricao());
        }

        return CategoryResponse.deEntidade(categoryRepository.save(categoria));
    }

    // Remove categoria por ID. Lança ResourceNotFoundException se não existir. Transacional.
    @Transactional
    public void deletar(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com o id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}