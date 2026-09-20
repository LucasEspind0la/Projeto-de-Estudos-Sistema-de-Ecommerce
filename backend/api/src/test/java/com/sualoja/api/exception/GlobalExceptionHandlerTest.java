package com.sualoja.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.CreateCategoryRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        User admin = new User();
        admin.setNome("Admin Teste");
        admin.setEmail("admin.exception@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        LoginRequest loginRequest = new LoginRequest("admin.exception@teste.com", "123456");
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar categoria inexistente")
    void deveRetornar404AoBuscarCategoriaInexistente() throws Exception {
        mockMvc.perform(get("/api/categorias/99999")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Não Encontrado"))
                .andExpect(jsonPath("$.mensagem").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar produto inexistente")
    void deveRetornar404AoBuscarProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/produtos/99999")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Não Encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar categoria com dados inválidos")
    void deveRetornar400AoCriarCategoriaComDadosInvalidos() throws Exception {
        String jsonInvalido = """
            {
                "nome": "",
                "descricao": "Teste"
            }
            """;

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Falha na Validação"))
                .andExpect(jsonPath("$.mensagens").exists());
    }

    @Test
    @DisplayName("Deve retornar 401 ao acessar endpoint protegido (carrinho) sem token")
    void deveRetornar401AoAcessarEndpointProtegidoSemToken() throws Exception {
        // O endpoint /api/carrinho exige autenticação. Sem token, o Spring Security bloqueia com 401.
        mockMvc.perform(get("/api/carrinho"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar criar categoria como cliente")
    void deveRetornar403AoTentarCriarCategoriaComoCliente() throws Exception {
        User cliente = new User();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente.exception@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(UserRole.CLIENTE);
        userRepository.save(cliente);

        LoginRequest loginRequest = new LoginRequest("cliente.exception@teste.com", "123456");
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        String tokenCliente = objectMapper.readTree(response).get("token").asText();

        CreateCategoryRequest request = new CreateCategoryRequest("Roupas", "Vestuário");

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenCliente)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}