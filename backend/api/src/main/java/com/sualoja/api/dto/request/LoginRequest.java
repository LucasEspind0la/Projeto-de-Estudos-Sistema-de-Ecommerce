package com.sualoja.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Dados que o cliente envia quando vai fazer LOGIN
public record LoginRequest(
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    
    @NotBlank(message = "A senha é obrigatória")
    String senha
) {}