package com.sualoja.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoRequestValidationCompleteTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validar ProductRequest com dados válidos")
    void validarProductRequestValido() {
        ProductVariantRequest variante = new ProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        ProductRequest request = new ProductRequest("Produto", "Desc", 1L, true, false, "url.jpg", List.of(variante));
        
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductRequest com nome em branco")
    void validarProductRequestNomeEmBranco() {
        ProductRequest request = new ProductRequest("", "Desc", 1L, true, false, null, null);
        
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductRequest com categoria nula")
    void validarProductRequestCategoriaNula() {
        ProductRequest request = new ProductRequest("Produto", "Desc", null, true, false, null, null);
        
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductVariantRequest com dados válidos")
    void validarProductVariantRequestValido() {
        ProductVariantRequest request = new ProductVariantRequest("Preto", "G", "SKU-001", 100.0, 10);
        
        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductVariantRequest com cor em branco")
    void validarProductVariantRequestCorEmBranco() {
        ProductVariantRequest request = new ProductVariantRequest("", "G", "SKU-001", 100.0, 10);
        
        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductVariantRequest com tamanho em branco")
    void validarProductVariantRequestTamanhoEmBranco() {
        ProductVariantRequest request = new ProductVariantRequest("Preto", "", "SKU-001", 100.0, 10);
        
        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductVariantRequest com SKU em branco")
    void validarProductVariantRequestSkuEmBranco() {
        ProductVariantRequest request = new ProductVariantRequest("Preto", "G", "", 100.0, 10);
        
        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductVariantRequest com preço nulo")
    void validarProductVariantRequestPrecoNulo() {
        ProductVariantRequest request = new ProductVariantRequest("Preto", "G", "SKU-001", null, 10);
        
        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar ProductVariantRequest com estoque nulo")
    void validarProductVariantRequestEstoqueNulo() {
        ProductVariantRequest request = new ProductVariantRequest("Preto", "G", "SKU-001", 100.0, null);
        
        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Validar CheckoutRequest")
    void validarCheckoutRequest() {
        CheckoutRequest request = new CheckoutRequest();
        
        Set<ConstraintViolation<CheckoutRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}