package com.sualoja.api.security;

import com.sualoja.api.model.entity.User;
import com.sualoja.api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // Este método roda ANTES de qualquer requisição chegar no Controller
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Pega o cabeçalho "Authorization" da requisição
        final String authHeader = request.getHeader("Authorization");
        
        // 2. Se não tiver cabeçalho ou não começar com "Bearer ", deixa passar (pode ser rota pública)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove a palavra "Bearer " para pegar só o token limpo
        final String token = authHeader.substring(7);

        try {
            // 4. Extrai o email de dentro do token
            final String email = jwtService.extrairEmail(token);

            // 5. Se tem email e o usuário ainda não está autenticado no sistema
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Busca o usuário no banco de dados
                User usuario = userRepository.findByEmail(email).orElse(null);

                // 6. Se o usuário existe e o token é válido (não expirado e assinado corretamente)
                if (usuario != null && jwtService.tokenValido(token, usuario)) {
                    
                    // Cria um objeto de autenticação do Spring com os dados do usuário
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            usuario, 
                            null, // Senha não é necessária aqui, pois o token já prova a identidade
                            usuario.getAuthorities() // Permissões (ROLE_ADMINISTRADOR, etc)
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 7. Informa ao Spring: "Este usuário está autenticado, pode prosseguir"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Se o token for inválido/falsificado, cai aqui. Não faz nada, a requisição seguirá sem autenticação.
        }

        // 8. Continua a cadeia de filtros (envia a requisição para o Controller)
        filterChain.doFilter(request, response);
    }
}