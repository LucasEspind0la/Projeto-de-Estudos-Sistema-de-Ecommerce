package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.CreateCategoryRequest;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.request.UpdateCategoryRequest;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.CategoryRepository;
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
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private String tokenCliente;

    @BeforeEach
    void setUp() throws Exception {
        User admin = new User();
        admin.setNome("Admin Categoria");
        admin.setEmail("admin.cat@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        User cliente = new User();
        cliente.setNome("Cliente Categoria");
        cliente.setEmail("cliente.cat@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(UserRole.CLIENTE);
        userRepository.save(cliente);

        LoginRequest loginAdmin = new LoginRequest("admin.cat@teste.com", "123456");
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginAdmin)))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(response).get("token").asText();

        LoginRequest loginCliente = new LoginRequest("cliente.cat@teste.com", "123456");
        response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCliente)))
                .andReturn().getResponse().getContentAsString();
        tokenCliente = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Admin deve criar categoria com sucesso")
    void adminDeveCriarCategoriaComSucesso() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Roupas", "Vestuário e acessórios");

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Roupas"))
                .andExpect(jsonPath("$.descricao").value("Vestuário e acessórios"));
    }

    @Test
    @DisplayName("Cliente não deve conseguir criar categoria")
    void clienteNaoDeveCriarCategoria() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Roupas", "Vestuário");

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenCliente)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar todas as categorias (Rota Pública)")
    void deveListarTodasCategorias() throws Exception {
        Category cat = new Category();
        cat.setNome("Eletrônicos");
        cat.setDescricao("Produtos eletrônicos");
        categoryRepository.save(cat);

        // GET em categorias é público no seu security config
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Admin deve atualizar categoria com sucesso")
    void adminDeveAtualizarCategoria() throws Exception {
        Category cat = new Category();
        cat.setNome("Antigo");
        cat.setDescricao("Descricao antiga");
        categoryRepository.save(cat);

        UpdateCategoryRequest request = new UpdateCategoryRequest("Novo", "Descricao nova");

        mockMvc.perform(put("/api/categorias/" + cat.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo"))
                .andExpect(jsonPath("$.descricao").value("Descricao nova"));
    }

    @Test
    @DisplayName("Admin deve deletar categoria com sucesso")
    void adminDeveDeletarCategoria() throws Exception {
        Category cat = new Category();
        cat.setNome("Para Deletar");
        categoryRepository.save(cat);

        mockMvc.perform(delete("/api/categorias/" + cat.getId())
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categorias/" + cat.getId())
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Não deve deletar categoria inexistente")
    void naoDeveDeletarCategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/api/categorias/9999")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }
}