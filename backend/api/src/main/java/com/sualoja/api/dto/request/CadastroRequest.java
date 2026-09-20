package com.sualoja.api.dto.request;

import com.sualoja.api.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Dados que o cliente envia quando vai se CADASTRAR
public record CadastroRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    String nome,
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha,
    
    @NotNull(message = "O papel é obrigatório")
    UserRole papel // Define se é ADMINISTRADOR ou CLIENTE
) {}