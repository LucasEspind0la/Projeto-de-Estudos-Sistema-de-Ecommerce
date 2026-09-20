package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.model.enums.UserRole;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductValidationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        User admin = new User();
        admin.setNome("Admin Val");
        admin.setEmail("admin.val@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin.val@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com nome em branco")
    void deveRetornar400NomeEmBranco() throws Exception {
        String json = """
            {
                "nome": "   ",
                "descricao": "Teste",
                "categoriaId": 1,
                "ativo": true,
                "destaque": false,
                "variantes": []
            }
            """;

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Falha na Validação"))
                .andExpect(jsonPath("$.mensagens.nome").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com categoriaId nulo")
    void deveRetornar400CategoriaNula() throws Exception {
        String json = """
            {
                "nome": "Produto Teste",
                "descricao": "Teste",
                "categoriaId": null,
                "ativo": true,
                "destaque": false,
                "variantes": []
            }
            """;

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Falha na Validação"))
                .andExpect(jsonPath("$.mensagens.categoriaId").exists());
    }
}