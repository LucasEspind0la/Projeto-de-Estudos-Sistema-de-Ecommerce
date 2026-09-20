package com.sualoja.api.config;

import com.sualoja.api.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado"))
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado"))
            )
            .authorizeHttpRequests(auth -> auth
                // 1. ROTAS PÚBLICAS (Devem vir PRIMEIRO)
                .requestMatchers("/api/auth/**").permitAll() // <--- AQUI ESTAVA O PROBLEMA!
                .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                
                // Documentação Swagger
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
        
                // 2. ROTAS DE CLIENTE AUTENTICADO
                .requestMatchers("/api/carrinho/**").authenticated()
                .requestMatchers("/api/pedidos/meus-pedidos").authenticated()
                .requestMatchers("/api/pedidos/finalizar").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/pedidos/{pedidoId}").authenticated() // Cliente vê seus detalhes
        
                // 3. ROTAS EXCLUSIVAS DE ADMINISTRADOR
                .requestMatchers(HttpMethod.POST, "/api/produtos/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/api/produtos/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMINISTRADOR")
                
                .requestMatchers("/api/pedidos/**").hasRole("ADMINISTRADOR") // Admin gerencia todos os pedidos (ex: mudar status)
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                
                // 4. QUALQUER OUTRA ROTA EXIGE AUTENTICAÇÃO
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}