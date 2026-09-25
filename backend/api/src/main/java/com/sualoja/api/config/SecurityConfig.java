package com.sualoja.api.config; // ⚠️ VERIFIQUE SE O PACOTE ESTÁ CORRETO NO SEU PROJETO

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. APLICA A CONFIGURAÇÃO DE CORS AQUI (MUITO IMPORTANTE)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. DESABILITA CSRF (Obrigatório para APIs stateless com JWT)
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                // Permite que qualquer um acesse as rotas de autenticação
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Se usar Swagger
                .anyRequest().authenticated()
            );
            // ... (Mantenha aqui suas configurações de JWT Filter se já tiver, ex: .addFilterBefore(...))
            
        return http.build();
    }

    // 3. DEFINE AS REGRAS DE QUEM PODE ACESSAR
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Adicione a URL EXATA da sua Vercel e o localhost para desenvolvimento
        configuration.setAllowedOrigins(Arrays.asList(
            "https://projeto-de-estudos-sistema-de-ecommerce-1hj77n4qg.vercel.app",
            "http://localhost:4200"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setAllowCredentials(true); // Essencial para JWT/Cookies
        configuration.setMaxAge(3600L); // Cache da preflight request por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}