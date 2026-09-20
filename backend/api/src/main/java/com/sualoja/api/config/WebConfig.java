package com.sualoja.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.diretorio}")
    private String diretorioBase;

    @Value("${app.upload.subdiretorio-produtos}")
    private String subdiretorioProdutos;

    /**
     * Configura o Spring para servir arquivos da pasta uploads como recursos estáticos.
     * Exemplo: http://localhost:8080/uploads/produtos/imagem.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Converte o caminho relativo em absoluto
        String caminhoAbsoluto = java.nio.file.Paths.get(diretorioBase).toAbsolutePath().normalize().toString();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + caminhoAbsoluto + "/");
    }
}