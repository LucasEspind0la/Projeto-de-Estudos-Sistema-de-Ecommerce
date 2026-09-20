package com.sualoja.api.dto.response;

// O que o servidor devolve após um login/cadastro de sucesso (o "crachá")
public record AuthResponse(
    String token,   // O JWT em si
    String email,   // Para confirmar quem logou
    String papel    // Para o frontend saber quais botões mostrar
) {}