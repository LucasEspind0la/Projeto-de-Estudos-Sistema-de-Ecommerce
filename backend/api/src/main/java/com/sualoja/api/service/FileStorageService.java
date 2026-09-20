package com.sualoja.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    // Tipos de arquivos permitidos (apenas imagens)
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // Extensões permitidas
    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @Value("${app.upload.diretorio}")
    private String diretorioBase;

    @Value("${app.upload.subdiretorio-produtos}")
    private String subdiretorioProdutos;

    /**
     * Salva uma imagem de produto no disco e retorna o nome do arquivo gerado.
     */
    public String salvarImagemProduto(MultipartFile arquivo) {
        // 1. Validar se o arquivo não está vazio
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio");
        }

        // 2. Validar o tipo do arquivo (MIME type)
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Aceitos: JPG, PNG, GIF, WEBP");
        }

        // 3. Validar a extensão do arquivo
        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal == null || !temExtensaoValida(nomeOriginal)) {
            throw new IllegalArgumentException("Extensão de arquivo não permitida. Aceitos: .jpg, .jpeg, .png, .gif, .webp");
        }

        // 4. Gerar um nome único para evitar conflitos
        String extensao = obterExtensao(nomeOriginal);
        String nomeUnico = UUID.randomUUID().toString() + extensao;

        // 5. Criar o caminho completo do arquivo
        Path diretorioDestino = Paths.get(diretorioBase, subdiretorioProdutos);
        
        try {
            Files.createDirectories(diretorioDestino); // Garante que a pasta exista
            Path caminhoCompleto = diretorioDestino.resolve(nomeUnico);
            
            // 6. Salvar o arquivo no disco
            Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
            
            return nomeUnico;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Deleta uma imagem do disco.
     */
    public void deletarImagem(String nomeArquivo) {
        try {
            Path caminho = Paths.get(diretorioBase, subdiretorioProdutos, nomeArquivo);
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            // Apenas loga o erro, não interrompe o fluxo
            System.err.println("Falha ao deletar arquivo: " + e.getMessage());
        }
    }

    private boolean temExtensaoValida(String nomeArquivo) {
        String extensao = obterExtensao(nomeArquivo);
        return EXTENSOES_PERMITIDAS.contains(extensao.toLowerCase());
    }

    private String obterExtensao(String nomeArquivo) {
        int indicePonto = nomeArquivo.lastIndexOf(".");
        if (indicePonto == -1) {
            return "";
        }
        return nomeArquivo.substring(indicePonto);
    }
}