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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private String tokenCliente;

    @BeforeEach
    void setUp() throws Exception {
        // Cria Admin
        User admin = new User();
        admin.setNome("Admin Dashboard");
        admin.setEmail("admin.dash@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        // Cria Cliente
        User cliente = new User();
        cliente.setNome("Cliente Dashboard");
        cliente.setEmail("cliente.dash@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(UserRole.CLIENTE);
        userRepository.save(cliente);

        // Login Admin
        String responseAdmin = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin.dash@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(responseAdmin).get("token").asText();

        // Login Cliente
        String responseCliente = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("cliente.dash@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenCliente = objectMapper.readTree(responseCliente).get("token").asText();
    }

    @Test
    @DisplayName("Deve retornar dados do dashboard com sucesso para admin")
    void deveRetornarDashboardComSucesso() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                // CORREÇÃO: Usando os nomes exatos dos campos do record DashboardResponse
                .andExpect(jsonPath("$.faturamentoTotal").exists())
                .andExpect(jsonPath("$.totalPedidos").exists())
                .andExpect(jsonPath("$.produtosEstoqueBaixo").exists())
                .andExpect(jsonPath("$.ultimosPedidos").isArray());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden para usuário não admin")
    void deveRetornar403ParaUsuarioNaoAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }
}