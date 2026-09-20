package com.sualoja.api.service;

import com.sualoja.api.dto.request.CreateProductRequest;
import com.sualoja.api.dto.response.ProductResponse;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @InjectMocks
    private ProductService productService;

    private Category categoria;
    private CreateProductRequest request;

    @BeforeEach
    void setUp() {
        categoria = new Category();
        categoria.setId(99L);
        categoria.setNome("Eletrônicos");

        request = new CreateProductRequest(
            "Smartphone",
            "Celular top de linha",
            99L, 
            true,
            true,
            List.of()
        );
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso")
    void deveCriarProdutoComSucesso() {
        // Arrange
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(categoria));
        
        // Simula o save retornando o produto com ID definido
        Product produtoSalvo = new Product();
        produtoSalvo.setId(1L);
        produtoSalvo.setNome("Smartphone");
        produtoSalvo.setCategoria(categoria); // Essencial para o DTO não dar NPE
        
        when(productRepository.save(any(Product.class))).thenReturn(produtoSalvo);
        
        // CORREÇÃO CRÍTICA: O seu serviço chama findById DEPOIS do save!
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(produtoSalvo));

        // Act
        ProductResponse response = productService.criar(request);

        // Assert
        assertNotNull(response);
        assertEquals("Smartphone", response.nome());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com categoria inexistente")
    void deveLancarExcecaoCategoriaInexistente() {
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.criar(request);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("categoria"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve excluir um produto com sucesso")
    void deveExcluirProdutoComSucesso() {
        // CORREÇÃO CRÍTICA: O serviço usa existsById, não findById!
        when(productRepository.existsById(any(Long.class))).thenReturn(true);

        // Act
        productService.deletar(1L);

        // CORREÇÃO CRÍTICA: O serviço usa deleteById, não delete!
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar produtos ativos com sucesso")
    void deveBuscarProdutosAtivosComSucesso() {
        Product produto = new Product();
        produto.setId(1L);
        produto.setNome("Produto Ativo");
        produto.setAtivo(true);
        produto.setCategoria(categoria); 
        
        // Ajuste o nome se o seu método for findByAtivoIsTrue()
        when(productRepository.findByAtivoTrue()).thenReturn(List.of(produto));

        List<ProductResponse> response = productService.buscarAtivos();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Produto Ativo", response.get(0).nome());
    }
}