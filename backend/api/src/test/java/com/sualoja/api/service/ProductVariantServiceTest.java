package com.sualoja.api.service;

import com.sualoja.api.dto.request.CreateProductVariantRequest;
import com.sualoja.api.dto.request.UpdateProductVariantRequest;
import com.sualoja.api.dto.response.ProductVariantResponse;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.Product;
import com.sualoja.api.model.entity.ProductVariant;
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
class ProductVariantServiceTest {

    @Mock private ProductVariantRepository variantRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks
    private ProductVariantService productVariantService;

    private ProductVariant variante;
    private Product produto;

    @BeforeEach
    void setUp() {
        produto = new Product();
        produto.setId(1L);
        produto.setNome("Teste");

        variante = new ProductVariant();
        variante.setId(1L);
        variante.setCor("Preto");
        variante.setTamanho("G");
        variante.setSku("SKU-001");
        variante.setPreco(BigDecimal.valueOf(100.00));
        variante.setEstoque(10);
        variante.setProduto(produto);
    }

    @Test
    @DisplayName("Deve criar variante com sucesso")
    void deveCriarVarianteComSucesso() {
        CreateProductVariantRequest request = new CreateProductVariantRequest("Preto", "G", "SKU-001", BigDecimal.valueOf(100.00), 10);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(produto));
        when(variantRepository.save(any(ProductVariant.class))).thenReturn(variante);

        ProductVariantResponse response = productVariantService.criar(1L, request);
        
        assertNotNull(response);
        assertEquals("Preto", response.cor());
    }

    @Test
    @DisplayName("Deve buscar variantes por produto")
    void deveBuscarVariantesPorProduto() {
        when(variantRepository.findByProdutoId(anyLong())).thenReturn(List.of(variante));

        List<ProductVariantResponse> responses = productVariantService.buscarPorProduto(1L);
        
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Deve buscar variante por ID")
    void deveBuscarVariantePorId() {
        when(variantRepository.findById(anyLong())).thenReturn(Optional.of(variante));

        ProductVariantResponse response = productVariantService.buscarPorId(1L);
        
        assertNotNull(response);
        assertEquals("Preto", response.cor());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar variante inexistente")
    void deveLancarExcecaoVarianteInexistente() {
        when(variantRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productVariantService.buscarPorId(99L);
        });
    }

    @Test
    @DisplayName("Deve atualizar variante com sucesso")
    void deveAtualizarVarianteComSucesso() {
        UpdateProductVariantRequest request = new UpdateProductVariantRequest("Azul", "M", "SKU-002", BigDecimal.valueOf(150.00), 15);
        when(variantRepository.findById(anyLong())).thenReturn(Optional.of(variante));
        when(variantRepository.save(any(ProductVariant.class))).thenReturn(variante);

        ProductVariantResponse response = productVariantService.atualizar(1L, request);
        
        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve deletar variante com sucesso")
    void deveDeletarVarianteComSucesso() {
        // Mockando exatamente o que o serviço faz: verifica existência e depois deleta por ID
        when(variantRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(variantRepository).deleteById(anyLong());

        // Executa o método
        productVariantService.deletar(1L);
        
        // CORREÇÃO FINAL: Verifica as chamadas exatas que o Mockito nos mostrou no erro
        verify(variantRepository, times(1)).existsById(1L);
        verify(variantRepository, times(1)).deleteById(1L);
    }
}