package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.AddToCartRequest;
import com.sualoja.api.model.entity.*;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User usuario;
    private ProductVariant varianteComPoucoEstoque;

    @BeforeEach
    void setUp() {
        // 1. Cria e salva o usuário real no banco (o save retorna o objeto com o ID gerado)
        usuario = new User();
        usuario.setNome("Cliente Teste");
        usuario.setEmail("cliente@teste.com");
        usuario.setSenha(passwordEncoder.encode("123456"));
        usuario.setPapel(UserRole.CLIENTE);
        usuario = userRepository.save(usuario); 

        // 2. Cria categoria, produto e variante
        Category categoria = new Category();
        categoria.setNome("Roupas");
        categoryRepository.save(categoria);

        Product produto = new Product();
        produto.setNome("Camiseta");
        produto.setDescricao("Camiseta básica");
        produto.setCategoria(categoria);
        produto.setAtivo(true);
        produto.setDestaque(false); 
        produto.setImagemUrl(null);   
        productRepository.save(produto);

        varianteComPoucoEstoque = new ProductVariant();
        varianteComPoucoEstoque.setProduto(produto);
        varianteComPoucoEstoque.setCor("Preta");
        varianteComPoucoEstoque.setTamanho("M");
        varianteComPoucoEstoque.setSku("CAM-PRT-M");
        varianteComPoucoEstoque.setPreco(BigDecimal.valueOf(49.90));
        varianteComPoucoEstoque.setEstoque(2);
        variantRepository.save(varianteComPoucoEstoque);
    }

    @Test
    void deveAdicionarAoCarrinhoComSucessoQuandoHaEstoque() throws Exception {
        // Cria o token de autenticação com o usuário REAL que está no banco
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        AddToCartRequest request = new AddToCartRequest(varianteComPoucoEstoque.getId(), 1);

        mockMvc.perform(post("/api/carrinho/adicionar")
                .with(authentication(authentication)) // <-- INJEÇÃO DIRETA E ROBUSTA NO CONTEXTO DE SEGURANÇA
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornarErroAoTentarAdicionarMaisDoQueOEstoqueDisponivel() throws Exception {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        AddToCartRequest request = new AddToCartRequest(varianteComPoucoEstoque.getId(), 5);

        mockMvc.perform(post("/api/carrinho/adicionar")
                .with(authentication(authentication)) // <-- INJEÇÃO DIRETA E ROBUSTA NO CONTEXTO DE SEGURANÇA
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}