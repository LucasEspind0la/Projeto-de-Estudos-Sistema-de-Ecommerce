package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.request.UpdateProductRequest;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.model.entity.Product;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.CategoryRepository;
import com.sualoja.api.repository.ProductRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private Long produtoId;
    private Long categoriaId;

    @BeforeEach
    void setUp() throws Exception {
        User admin = new User();
        admin.setNome("Admin Prod");
        admin.setEmail("admin.prod@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin.prod@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(response).get("token").asText();

        Category cat = new Category();
        cat.setNome("Teste");
        categoryRepository.save(cat);
        categoriaId = cat.getId();

        Product prod = new Product();
        prod.setNome("Produto Original");
        prod.setDescricao("Desc");
        prod.setCategoria(cat);
        prod.setAtivo(true);
        prod.setDestaque(false);
        productRepository.save(prod);
        produtoId = prod.getId();
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso (PUT)")
    void deveAtualizarProdutoComSucesso() throws Exception {
        // CORREÇÃO: Adicionado categoriaId (5 parâmetros: nome, descricao, categoriaId, ativo, destaque)
        UpdateProductRequest request = new UpdateProductRequest("Produto Atualizado", "Nova Desc", categoriaId, true, false);

        mockMvc.perform(put("/api/produtos/" + produtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Produto Atualizado"));
    }

    @Test
    @DisplayName("Deve alternar status ativo/inativo com sucesso (PATCH)")
    void deveAlternarStatusAtivo() throws Exception {
        mockMvc.perform(patch("/api/produtos/" + produtoId + "/alternar-ativo")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false)); // Era true, agora deve ser false
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso (DELETE)")
    void deveDeletarProdutoComSucesso() throws Exception {
        mockMvc.perform(delete("/api/produtos/" + produtoId)
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar produto inexistente")
    void deveRetornar404AoBuscarProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/produtos/99999")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }
}
