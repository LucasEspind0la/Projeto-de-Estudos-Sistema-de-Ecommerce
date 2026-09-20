package com.sualoja.api.service;

import com.sualoja.api.dto.request.CreateProductRequest;
import com.sualoja.api.dto.request.CreateProductVariantRequest;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.model.entity.Product;
import com.sualoja.api.repository.CategoryRepository;
import com.sualoja.api.repository.ProductRepository;
import com.sualoja.api.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceBranchCoverageTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private ProductService productService;

    private Product produto;
    private Category categoria;

    @BeforeEach
    void setUp() {
        categoria = new Category();
        categoria.setId(1L);
        
        produto = new Product();
        produto.setId(1L);
        produto.setNome("Produto");
        produto.setCategoria(categoria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com categoria inexistente")
    void deveLancarExcecaoCategoriaInexistente() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        CreateProductRequest request = new CreateProductRequest("Produto", "Desc", 99L, true, false, null);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.criar(request);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com SKU duplicado")
    void deveLancarExcecaoSkuDuplicado() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(productVariantRepository.existsBySku("SKU-001")).thenReturn(true);
        
        // CORREÇÃO: Usando BigDecimal.valueOf(100.0) em vez de 100.0
        CreateProductVariantRequest variante = new CreateProductVariantRequest("Preto", "G", "SKU-001", BigDecimal.valueOf(100.0), 10);
        CreateProductRequest request = new CreateProductRequest("Produto", "Desc", 1L, true, false, List.of(variante));
        
        assertThrows(IllegalArgumentException.class, () -> {
            productService.criar(request);
        });
    }

    @Test
    @DisplayName("Deve criar produto sem variantes com sucesso")
    void deveCriarProdutoSemVariantes() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(productRepository.save(any(Product.class))).thenReturn(produto);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(produto));
        
        CreateProductRequest request = new CreateProductRequest("Produto", "Desc", 1L, true, false, null);
        
        assertDoesNotThrow(() -> {
            productService.criar(request);
        });
    }

    @Test
    @DisplayName("Deve buscar todos os produtos")
    void deveBuscarTodosProdutos() {
        when(productRepository.findAll()).thenReturn(List.of(produto));
        
        var produtos = productService.buscarTodos();
        
        assertNotNull(produtos);
        assertEquals(1, produtos.size());
    }

    @Test
    @DisplayName("Deve buscar produtos ativos")
    void deveBuscarProdutosAtivos() {
        when(productRepository.findByAtivoTrue()).thenReturn(List.of(produto));
        
        var produtos = productService.buscarAtivos();
        
        assertNotNull(produtos);
        assertEquals(1, produtos.size());
    }
}