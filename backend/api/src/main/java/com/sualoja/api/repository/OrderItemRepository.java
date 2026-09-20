package com.sualoja.api.repository;

import com.sualoja.api.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Lista todos os itens de um pedido específico
    List<OrderItem> findByPedidoId(Long pedidoId);
}