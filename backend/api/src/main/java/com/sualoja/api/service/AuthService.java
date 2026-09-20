package com.sualoja.api.service;

import com.sualoja.api.dto.request.CadastroRequest;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.response.AuthResponse;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.repository.UserRepository;
import com.sualoja.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injetado pelo Spring, usa BCrypt
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Gerenciador de login do Spring

    public AuthResponse cadastrar(CadastroRequest requisicao) {
        // Verifica se já existe alguém com esse email
        if (userRepository.existsByEmail(requisicao.email())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }

        // Cria o usuário. O passwordEncoder embaralha a senha (BCrypt) antes de salvar
        User usuario = User.builder()
            .nome(requisicao.nome())
            .email(requisicao.email())
            .senha(passwordEncoder.encode(requisicao.senha())) 
            .papel(requisicao.papel())
            .build();

        userRepository.save(usuario);

        // Gera o token JWT para o usuário já sair logado após o cadastro
        String token = jwtService.gerarToken(usuario);

        return new AuthResponse(token, usuario.getEmail(), usuario.getPapel().name());
    }

    public AuthResponse login(LoginRequest requisicao) {
        // O AuthenticationManager verifica se o email e a senha batem com o que está no banco
        // Se a senha estiver errada, ele lança uma exceção automaticamente
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                requisicao.email(),
                requisicao.senha()
            )
        );

        // Se chegou aqui, a senha está correta. Busca o usuário para gerar o token
        User usuario = userRepository.findByEmail(requisicao.email())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario);

        return new AuthResponse(token, usuario.getEmail(), usuario.getPapel().name());
    }
}