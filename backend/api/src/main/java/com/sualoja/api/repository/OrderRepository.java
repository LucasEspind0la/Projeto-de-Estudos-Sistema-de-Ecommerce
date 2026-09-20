package com.sualoja.api.repository;

import com.sualoja.api.model.entity.Order;
import com.sualoja.api.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findTop5ByOrderByCriadoEmDesc();
    
    // Lista todos os pedidos ordenados do mais recente para o mais antigo
    List<Order> findAllByOrderByCriadoEmDesc();

    @Query("SELECT COALESCE(SUM(o.valorTotal), 0) FROM Order o")
    BigDecimal calcularFaturamentoTotal();
}
