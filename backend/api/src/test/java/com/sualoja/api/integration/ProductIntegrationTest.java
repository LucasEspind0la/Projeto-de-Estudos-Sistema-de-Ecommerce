package com.sualoja.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sualoja.api.dto.request.CreateProductRequest;
import com.sualoja.api.model.entity.Category;
import com.sualoja.api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName; // <-- IMPORT ADICIONADO AQUI
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category categoria;

    @BeforeEach
    void setUp() {
        categoria = new Category();
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Produtos de tecnologia");
        categoryRepository.save(categoria);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void deveCriarProdutoComSucesso() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
            "Smartphone",
            "Celular de última geração",
            categoria.getId(),
            true,
            true,
            List.of()
        );

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Smartphone"))
                .andExpect(jsonPath("$.categoriaId").value(categoria.getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void deveRetornarErroAoCriarProdutoComCategoriaInexistente() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
            "Produto Inválido",
            "Descrição",
            9999L,
            true,
            false,
            List.of()
        );

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void deveFazerUploadDeImagemComSucesso() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
            "Notebook",
            "Notebook gamer",
            categoria.getId(),
            true,
            true,
            List.of()
        );

        String responseProduto = mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long produtoId = objectMapper.readTree(responseProduto).get("id").asLong();

        MockMultipartFile arquivoImagem = new MockMultipartFile(
            "imagem",
            "teste.jpg",
            "image/jpeg",
            "conteudo da imagem fake".getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/produtos/" + produtoId + "/imagem")
                .file(arquivoImagem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagemUrl").exists());
    }

    // --- NOVOS TESTES ADICIONADOS PARA COBERTURA ---

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar criar produto sem autenticação")
    void deveRetornar401AoTentarCriarProdutoSemAutenticacao() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
            "Produto Sem Token", "Descricao", categoria.getId(), true, true, List.of()
        );

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve listar produtos ativos com sucesso (Rota Pública)")
    void deveListarProdutosAtivosComSucesso() throws Exception {
        mockMvc.perform(get("/api/produtos/ativos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}