package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.LoginRequest;
import com.sualoja.api.dto.request.UpdateCartItemRequest;
import com.sualoja.api.model.entity.*;
import com.sualoja.api.model.enums.UserRole;
import com.sualoja.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductVariantRepository variantRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenCliente;
    private Long varianteId;
    private Long itemId;

    @BeforeEach
    void setUp() throws Exception {
        User cliente = new User();
        cliente.setNome("Cliente Cart");
        cliente.setEmail("cliente.cart@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(UserRole.CLIENTE);
        userRepository.save(cliente);

        String resp = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("cliente.cart@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenCliente = objectMapper.readTree(resp).get("token").asText();

        Category cat = new Category(); cat.setNome("Teste"); categoryRepository.save(cat);
        Product prod = new Product(); prod.setNome("Teste"); prod.setCategoria(cat); prod.setAtivo(true); prod.setDestaque(false);
        productRepository.save(prod);
        
        ProductVariant variante = new ProductVariant();
        variante.setProduto(prod); variante.setCor("Azul"); variante.setTamanho("M"); variante.setSku("SKU-CART");
        variante.setPreco(BigDecimal.valueOf(50.00)); variante.setEstoque(20);
        variantRepository.save(variante);
        varianteId = variante.getId();

        // CORREÇÃO SÊNIOR: Criar o carrinho e o item DIRETAMENTE no banco (100% confiável)
        Cart cart = new Cart();
        cart.setUsuario(cliente);
        
        CartItem item = new CartItem();
        item.setCarrinho(cart); // Usando o mesmo nome de campo que funcionou no OrderIntegrationTest
        item.setVarianteProduto(variante);
        item.setQuantidade(1);
        
        cart.setItens(new ArrayList<>(List.of(item)));
        cartRepository.save(cart);
        
        // Agora temos o ID do item com certeza absoluta
        this.itemId = item.getId();
    }

    @Test
    @DisplayName("Deve atualizar quantidade do item no carrinho")
    void deveAtualizarQuantidade() throws Exception {
        UpdateCartItemRequest request = new UpdateCartItemRequest(3);

        mockMvc.perform(put("/api/carrinho/itens/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenCliente)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].quantidade").value(3));
    }

    @Test
    @DisplayName("Deve remover item do carrinho")
    void deveRemoverItem() throws Exception {
        mockMvc.perform(delete("/api/carrinho/itens/" + itemId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve limpar o carrinho completamente")
    void deveLimparCarrinho() throws Exception {
        mockMvc.perform(delete("/api/carrinho/limpar")
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isNoContent());
    }
}