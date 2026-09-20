package com.sualoja.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Handler global de exceções REST. Captura erros em toda a aplicação e retorna respostas padronizadas.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata ResourceNotFoundException -> Retorna 404 NOT_FOUND com mensagem do erro.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(ResourceNotFoundException excecao) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.NOT_FOUND.value());
        erro.put("erro", "Não Encontrado");
        erro.put("mensagem", excecao.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // Trata IllegalArgumentException -> Retorna 400 BAD_REQUEST com mensagem do erro.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoIlegal(IllegalArgumentException excecao) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Requisição Inválida");
        erro.put("mensagem", excecao.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // Trata erros de validação (@Valid) -> Retorna 400 BAD_REQUEST com erros por campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException excecao) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Falha na Validação");
        
        Map<String, String> errosDeCampo = new HashMap<>();
        for (FieldError erroDeCampo : excecao.getBindingResult().getFieldErrors()) {
            errosDeCampo.put(erroDeCampo.getField(), erroDeCampo.getDefaultMessage());
        }
        erro.put("mensagens", errosDeCampo);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}