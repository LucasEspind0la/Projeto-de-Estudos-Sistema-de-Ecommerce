package com.sualoja.api.service;

import com.sualoja.api.dto.request.UpdateProductRequest;
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
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceGapCoverageTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductVariantRepository productVariantRepository;
    @Mock private FileStorageService fileStorageService;

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
        produto.setNome("Produto Original");
        produto.setAtivo(true);
        produto.setDestaque(false);
        produto.setCategoria(categoria);
    }

    @Test
    @DisplayName("Deve atualizar produto parcialmente com sucesso")
    void deveAtualizarProdutoParcialmente() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(productRepository.save(any(Product.class))).thenReturn(produto);

        // CORREÇÃO: Passar null para ativo e destaque para não alterá-los
        UpdateProductRequest request = new UpdateProductRequest("Novo Nome", null, null, null, null);
        productService.atualizar(1L, request);

        assertEquals("Novo Nome", produto.getNome());
        assertFalse(produto.getDestaque()); // Não foi alterado
        assertTrue(produto.getAtivo()); // Não foi alterado
    }

    @Test
    @DisplayName("Deve alternar status ativo com sucesso")
    void deveAlternarStatusAtivo() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(productRepository.save(any(Product.class))).thenReturn(produto);

        productService.alternarStatusAtivo(1L);
        assertFalse(produto.getAtivo());
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deletar(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    void deveLancarExcecaoDeletarInexistente() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deletar(1L));
    }

    @Test
    @DisplayName("Deve atualizar imagem com sucesso (sem imagem anterior)")
    void deveAtualizarImagemSemAnterior() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(fileStorageService.salvarImagemProduto(any())).thenReturn("nova-imagem.jpg");
        when(productRepository.save(any(Product.class))).thenReturn(produto);

        MockMultipartFile arquivo = new MockMultipartFile("imagem", "teste.jpg", "image/jpeg", "conteudo".getBytes());
        productService.atualizarImagem(1L, arquivo);

        assertEquals("nova-imagem.jpg", produto.getImagemUrl());
    }

    @Test
    @DisplayName("Deve atualizar imagem com sucesso (deletando imagem anterior)")
    void deveAtualizarImagemComAnterior() {
        produto.setImagemUrl("imagem-antiga.jpg");
        when(productRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(fileStorageService.salvarImagemProduto(any())).thenReturn("nova-imagem.jpg");
        when(productRepository.save(any(Product.class))).thenReturn(produto);

        MockMultipartFile arquivo = new MockMultipartFile("imagem", "teste.jpg", "image/jpeg", "conteudo".getBytes());
        productService.atualizarImagem(1L, arquivo);

        verify(fileStorageService, times(1)).deletarImagem("imagem-antiga.jpg");
        assertEquals("nova-imagem.jpg", produto.getImagemUrl());
    }
}