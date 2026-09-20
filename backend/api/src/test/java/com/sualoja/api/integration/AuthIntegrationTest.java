package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Cria um usuário CLIENTE para os testes
        User cliente = new User();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(UserRole.CLIENTE);
        userRepository.save(cliente);

        // Cria um usuário ADMIN para os testes
        User admin = new User();
        admin.setNome("Admin Teste");
        admin.setEmail("admin@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);
    }

    @Test
    void deveRealizarLoginComCredenciaisValidas() throws Exception {
        LoginRequest request = new LoginRequest("cliente@teste.com", "123456");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("cliente@teste.com"));
    }

    @Test
    void deveRetornarErroAoTentarLoginComCredenciaisInvalidas() throws Exception {
        LoginRequest request = new LoginRequest("cliente@teste.com", "senha_errada");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void deveRetornarErroAoAcessarRotaProtegidaSemToken() throws Exception {
        // Tenta acessar o carrinho sem estar autenticado
        mockMvc.perform(get("/api/carrinho"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void deveRetornarErroAoAcessarRotaAdminSendoCliente() throws Exception {
        // Tenta criar um produto (rota ADMIN) logado como CLIENTE
        mockMvc.perform(post("/api/produtos")
                .with(user("cliente@teste.com").roles("CLIENTE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden()); // 403
    }
}