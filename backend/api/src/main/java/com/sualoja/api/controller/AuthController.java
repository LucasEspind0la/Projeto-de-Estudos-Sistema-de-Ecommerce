package com.sualoja.api.controller;

import com.sualoja.api.dto.request.CadastroRequest;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.response.AuthResponse;
import com.sualoja.api.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login e cadastro")
public class AuthController {

    private final AuthService authService;

    // Endpoint público para criar nova conta
    @PostMapping("/cadastrar")
    public ResponseEntity<AuthResponse> cadastrar(@RequestBody @Valid CadastroRequest requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(requisicao));
    }

    // Endpoint público para fazer login e receber o token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest requisicao) {
        return ResponseEntity.ok(authService.login(requisicao));
    }
}