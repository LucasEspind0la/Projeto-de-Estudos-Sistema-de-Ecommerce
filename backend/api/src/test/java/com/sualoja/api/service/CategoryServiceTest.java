package com.sualoja.api.service;

import com.sualoja.api.dto.request.CreateCategoryRequest;
import com.sualoja.api.dto.response.CategoryResponse;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category categoria;

    @BeforeEach
    void setUp() {
        categoria = new Category();
        categoria.setId(99L);
        categoria.setNome("Roupas");
    }

    @Test
    @DisplayName("Deve excluir categoria com sucesso")
    void deveExcluirCategoriaComSucesso() {
        // O serviço usa existsById e depois deleteById
        when(categoryRepository.existsById(anyLong())).thenReturn(true);

        // Act
        categoryService.deletar(1L);

        // Assert
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir categoria inexistente")
    void deveLancarExcecaoAoExcluirCategoriaInexistente() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deletar(1L);
        });

        assertTrue(exception.getMessage().contains("Categoria não encontrada"));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoriaComSucesso() {
        CreateCategoryRequest request = new CreateCategoryRequest("Calçados", "Sapatos e tênis");
        
        Category categoriaSalva = new Category();
        categoriaSalva.setId(1L);
        categoriaSalva.setNome("Calçados");
        
        when(categoryRepository.save(any(Category.class))).thenReturn(categoriaSalva);

        CategoryResponse response = categoryService.criar(request);

        assertNotNull(response);
        assertEquals("Calçados", response.nome());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}