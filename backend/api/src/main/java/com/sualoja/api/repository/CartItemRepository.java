package com.sualoja.api.repository;

import com.sualoja.api.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Lista todos os itens de um carrinho específico
    List<CartItem> findByCarrinhoId(Long carrinhoId);
    // Busca um item específico dentro de um carrinho (para saber se já existe)
    Optional<CartItem> findByCarrinhoIdAndVarianteProdutoId(Long carrinhoId, Long varianteId);
    // Verifica se um carrinho tem algum item
    boolean existsByCarrinhoId(Long carrinhoId);
}