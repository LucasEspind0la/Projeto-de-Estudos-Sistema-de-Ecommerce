package com.sualoja.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceExpansionTest {

    private FileStorageService fileStorageService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("uploads-test");
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "diretorioBase", tempDir.toString());
        ReflectionTestUtils.setField(fileStorageService, "subdiretorioProdutos", "produtos");
    }

    @Test
    @DisplayName("Deve salvar imagem com sucesso")
    void deveSalvarImagemComSucesso() {
        MockMultipartFile arquivo = new MockMultipartFile("imagem", "teste.jpg", "image/jpeg", "conteudo".getBytes());
        
        String nomeArquivo = fileStorageService.salvarImagemProduto(arquivo);
        
        assertNotNull(nomeArquivo);
        assertTrue(nomeArquivo.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve("produtos").resolve(nomeArquivo)));
    }

    @Test
    @DisplayName("Deve lançar exceção para arquivo vazio")
    void deveLancarExcecaoArquivoVazio() {
        MockMultipartFile arquivoVazio = new MockMultipartFile("imagem", "vazio.jpg", "image/jpeg", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.salvarImagemProduto(arquivoVazio);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo MIME inválido")
    void deveLancarExcecaoTipoMimeInvalido() {
        MockMultipartFile arquivoPdf = new MockMultipartFile("imagem", "doc.pdf", "application/pdf", "conteudo".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.salvarImagemProduto(arquivoPdf);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para extensão inválida")
    void deveLancarExcecaoExtensaoInvalida() {
        // Content type válido, mas extensão inválida
        MockMultipartFile arquivoTxt = new MockMultipartFile("imagem", "doc.txt", "image/jpeg", "conteudo".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.salvarImagemProduto(arquivoTxt);
        });
    }

    @Test
    @DisplayName("Deve deletar imagem com sucesso (sem lançar exceção)")
    void deveDeletarImagemComSucesso() {
        // Não deve lançar exceção mesmo se o arquivo não existir
        assertDoesNotThrow(() -> {
            fileStorageService.deletarImagem("arquivo-inexistente.jpg");
        });
    }
}