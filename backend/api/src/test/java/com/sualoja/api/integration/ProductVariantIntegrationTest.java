package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.CreateProductVariantRequest;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.request.UpdateProductVariantRequest;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.model.entity.Product;
import com.sualoja.api.model.entity.ProductVariant;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.CategoryRepository;
import com.sualoja.api.repository.ProductRepository;
import com.sualoja.api.repository.ProductVariantRepository;
import com.sualoja.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductVariantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private Long produtoId;

    @BeforeEach
    void setUp() throws Exception {
        User admin = new User();
        admin.setNome("Admin Variante");
        admin.setEmail("admin.variante@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin.variante@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(response).get("token").asText();

        Category categoria = new Category();
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Produtos eletrônicos");
        categoryRepository.save(categoria);

        Product produto = new Product();
        produto.setNome("Smartphone");
        produto.setDescricao("Celular top de linha");
        produto.setCategoria(categoria);
        produto.setAtivo(true);
        produto.setDestaque(false);
        productRepository.save(produto);
        
        produtoId = produto.getId();
    }

    @Test
    @DisplayName("Deve criar variante de produto com sucesso")
    void deveCriarVarianteComSucesso() throws Exception {
        // CORREÇÃO: Ordem correta - (cor, tamanho, sku, preco, estoque)
        CreateProductVariantRequest request = new CreateProductVariantRequest(
            "Preto", "G", "SKU-TESTE-01", BigDecimal.valueOf(1500.00), 10
        );

        mockMvc.perform(post("/api/produtos/" + produtoId + "/variacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cor").value("Preto"))
                .andExpect(jsonPath("$.estoque").value(10));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar variante com dados inválidos")
    void deveRetornar400AoCriarVarianteInvalida() throws Exception {
        // Dados inválidos para acionar o GlobalExceptionHandler
        CreateProductVariantRequest request = new CreateProductVariantRequest(
            "", "G", "", BigDecimal.valueOf(-100.00), -5
        );

        mockMvc.perform(post("/api/produtos/" + produtoId + "/variacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar variantes de um produto")
    void deveListarVariantesDoProduto() throws Exception {
        ProductVariant variante = new ProductVariant();
        variante.setProduto(productRepository.findById(produtoId).get());
        variante.setCor("Azul");
        variante.setTamanho("M");
        variante.setPreco(BigDecimal.valueOf(1000.00));
        variante.setEstoque(5);
        variante.setSku("SKU-TESTE-02");
        variantRepository.save(variante);

        mockMvc.perform(get("/api/produtos/" + produtoId + "/variacoes")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Deve buscar variante por ID")
    void deveBuscarVariantePorId() throws Exception {
        ProductVariant variante = new ProductVariant();
        variante.setProduto(productRepository.findById(produtoId).get());
        variante.setCor("Vermelho");
        variante.setTamanho("P");
        variante.setPreco(BigDecimal.valueOf(800.00));
        variante.setEstoque(2);
        variante.setSku("SKU-TESTE-03");
        variantRepository.save(variante);

        mockMvc.perform(get("/api/produtos/" + produtoId + "/variacoes/" + variante.getId())
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor").value("Vermelho"));
    }

    @Test
    @DisplayName("Deve atualizar variante com sucesso")
    void deveAtualizarVarianteComSucesso() throws Exception {
        ProductVariant variante = new ProductVariant();
        variante.setProduto(productRepository.findById(produtoId).get());
        variante.setCor("Branco");
        variante.setTamanho("GG");
        variante.setPreco(BigDecimal.valueOf(1200.00));
        variante.setEstoque(8);
        variante.setSku("SKU-TESTE-04");
        variantRepository.save(variante);

        // CORREÇÃO: 5 parâmetros - (cor, tamanho, sku, preco, estoque)
        UpdateProductVariantRequest request = new UpdateProductVariantRequest(
            "Preto", "G", "SKU-TESTE-04-UPD", BigDecimal.valueOf(1500.00), 15
        );

        mockMvc.perform(put("/api/produtos/" + produtoId + "/variacoes/" + variante.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor").value("Preto"))
                .andExpect(jsonPath("$.estoque").value(15));
    }

    @Test
    @DisplayName("Deve deletar variante com sucesso")
    void deveDeletarVarianteComSucesso() throws Exception {
        ProductVariant variante = new ProductVariant();
        variante.setProduto(productRepository.findById(produtoId).get());
        variante.setCor("Amarelo");
        variante.setTamanho("U");
        variante.setPreco(BigDecimal.valueOf(500.00));
        variante.setEstoque(1);
        variante.setSku("SKU-TESTE-05");
        variantRepository.save(variante);

        mockMvc.perform(delete("/api/produtos/" + produtoId + "/variacoes/" + variante.getId())
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }
}