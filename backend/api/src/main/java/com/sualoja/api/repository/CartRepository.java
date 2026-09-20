package com.sualoja.api.repository;

import com.sualoja.api.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Busca o carrinho ativo de um usuário específico
    Optional<Cart> findByUsuarioId(Long usuarioId);
    // Verifica se o usuário já tem um carrinho
    boolean existsByUsuarioId(Long usuarioId);
}