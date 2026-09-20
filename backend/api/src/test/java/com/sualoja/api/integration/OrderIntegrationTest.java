package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.LoginRequest;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductVariantRepository variantRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenCliente;
    private String tokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        User cliente = new User();
        cliente.setNome("Cliente Order");
        cliente.setEmail("cliente.order@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(UserRole.CLIENTE);
        userRepository.save(cliente);

        User admin = new User();
        admin.setNome("Admin Order");
        admin.setEmail("admin.order@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setPapel(UserRole.ADMINISTRADOR);
        userRepository.save(admin);

        String respCliente = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("cliente.order@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenCliente = objectMapper.readTree(respCliente).get("token").asText();

        String respAdmin = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin.order@teste.com", "123456"))))
                .andReturn().getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(respAdmin).get("token").asText();

        Category cat = new Category(); cat.setNome("Teste"); categoryRepository.save(cat);
        Product prod = new Product(); prod.setNome("Teste"); prod.setCategoria(cat); prod.setAtivo(true); prod.setDestaque(false);
        productRepository.save(prod);
        
        ProductVariant variante = new ProductVariant();
        variante.setProduto(prod); variante.setCor("Preto"); variante.setTamanho("U"); variante.setSku("SKU-ORDER");
        variante.setPreco(BigDecimal.valueOf(100.00)); variante.setEstoque(10);
        variantRepository.save(variante);

        // Criar o carrinho e o item DIRETAMENTE no banco para garantir 100% que o estado está correto
        Cart cart = new Cart();
        cart.setUsuario(cliente);
        
        CartItem item = new CartItem();
        // NOTA: Se sua entidade CartItem usar o nome 'cart' em vez de 'carrinho', mude para item.setCart(cart);
        item.setCarrinho(cart); 
        item.setVarianteProduto(variante);
        item.setQuantidade(2);
        
        cart.setItens(new ArrayList<>(List.of(item)));
        cartRepository.save(cart);
    }

    @Test
    @DisplayName("Deve finalizar pedido com sucesso")
    void deveFinalizarPedidoComSucesso() throws Exception {
        // CORREÇÃO: andDo() vem DEPOIS do perform()
        mockMvc.perform(post("/api/pedidos/finalizar")
                .header("Authorization", "Bearer " + tokenCliente))
                .andDo(MockMvcResultHandlers.print()) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve listar meus pedidos")
    void deveListarMeusPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos/meus-pedidos")
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Admin deve listar todos os pedidos")
    void adminDeveListarTodosPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID")
    void deveBuscarPedidoPorId() throws Exception {
        String resp = mockMvc.perform(post("/api/pedidos/finalizar").header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Long pedidoId = objectMapper.readTree(resp).get("id").asLong();

        mockMvc.perform(get("/api/pedidos/" + pedidoId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedidoId));
    }

    @Test
    @DisplayName("Admin deve atualizar status do pedido")
    void adminDeveAtualizarStatus() throws Exception {
        String resp = mockMvc.perform(post("/api/pedidos/finalizar").header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Long pedidoId = objectMapper.readTree(resp).get("id").asLong();

        mockMvc.perform(patch("/api/pedidos/" + pedidoId + "/status?status=PAGO")
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGO"));
    }
}