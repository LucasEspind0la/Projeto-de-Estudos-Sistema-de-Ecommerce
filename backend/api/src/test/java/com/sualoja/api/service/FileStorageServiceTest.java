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

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("uploads-test");
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "diretorioBase", tempDir.toString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar upload de arquivo vazio")
    void deveLancarExcecaoAoTentarUploadDeArquivoVazio() {
        MockMultipartFile arquivoVazio = new MockMultipartFile("imagem", "vazio.jpg", "image/jpeg", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.salvarImagemProduto(arquivoVazio);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar upload com tipo MIME inválido")
    void deveLancarExcecaoTipoMimeInvalido() {
        MockMultipartFile arquivoInvalido = new MockMultipartFile("imagem", "documento.pdf", "application/pdf", "conteudo".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.salvarImagemProduto(arquivoInvalido);
        });
    }
}