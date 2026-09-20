package com.sualoja.api.security;

import com.sualoja.api.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Lê a chave secreta e o tempo do application.yml
    @Value("${app.jwt.chave-secreta}")
    private String chaveSecreta;

    @Value("${app.jwt.expiracao-ms}")
    private long expiracaoMs;

    // Cria o token JWT assinado com os dados do usuário
    public String gerarToken(User usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getPapel().name()); // Adiciona o papel (ADMIN/CLIENTE) dentro do token
        
        return Jwts.builder()
            .claims(claims)                          // Dados personalizados
            .subject(usuario.getEmail())             // O "dono" do token
            .issuedAt(new Date(System.currentTimeMillis())) // Data de criação
            .expiration(new Date(System.currentTimeMillis() + expiracaoMs)) // Data de expiração
            .signWith(getChaveAssinatura())          // Assina com a chave secreta (garante que não foi falsificado)
            .compact();                              // Gera a string final do token
    }

    // Lê o email que está gravado dentro do token
    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    // Verifica se o token é válido e se pertence a este usuário específico
    public boolean tokenValido(String token, User usuario) {
        final String email = extrairEmail(token);
        return (email.equals(usuario.getEmail())) && !tokenExpirado(token);
    }

    // --- Métodos auxiliares privados ---

    private boolean tokenExpirado(String token) {
        return extrairClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
            .verifyWith(getChaveAssinatura()) // Usa a chave para "destrancar" e ler o token
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // Converte a string da chave secreta em um formato criptográfico que o JWT entende
    private SecretKey getChaveAssinatura() {
        byte[] keyBytes = chaveSecreta.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}