package com.sualoja.api.service;

import com.sualoja.api.dto.request.CadastroRequest;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.response.AuthResponse;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.UserRepository;
import com.sualoja.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User usuario;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@test.com");
        usuario.setSenha("senha123");
        usuario.setPapel(UserRole.CLIENTE);
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() {
        LoginRequest request = new LoginRequest("teste@test.com", "123456");
        Authentication authMock = mock(Authentication.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(userRepository.findByEmail("teste@test.com")).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(usuario)).thenReturn("token-falso");

        AuthResponse response = authService.login(request);
        
        assertNotNull(response);
        assertEquals("token-falso", response.token());
        assertEquals("teste@test.com", response.email());
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() {
        CadastroRequest request = new CadastroRequest("Novo", "novo@test.com", "123456", UserRole.CLIENTE);
        when(userRepository.findByEmail("novo@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");
        
        // Retorna o usuário mockado quando save for chamado
        when(userRepository.save(any(User.class))).thenReturn(usuario);
        
        // CORREÇÃO: Mockar para QUALQUER instância de User, pois o service cria uma nova internamente
        when(jwtService.gerarToken(any(User.class))).thenReturn("token-falso");

        AuthResponse response = authService.cadastrar(request);
        
        assertNotNull(response);
        assertEquals("token-falso", response.token());
    }
}