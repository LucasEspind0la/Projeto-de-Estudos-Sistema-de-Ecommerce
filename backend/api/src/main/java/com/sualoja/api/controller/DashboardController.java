package com.sualoja.api.controller;

import com.sualoja.api.dto.response.DashboardResponse;
import com.sualoja.api.dto.response.OrderResponse;
import com.sualoja.api.repository.OrderRepository;
import com.sualoja.api.repository.ProductVariantRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal; 
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Admin", description = "Métricas exclusivas para o painel administrativo")
public class DashboardController {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        BigDecimal faturamento = orderRepository.calcularFaturamentoTotal();
        long totalPedidos = orderRepository.count();
        long estoqueBaixo = variantRepository.contarEstoqueBaixo();
        
        List<OrderResponse> ultimosPedidos = orderRepository.findTop5ByOrderByCriadoEmDesc()
                .stream()
                .map(OrderResponse::deEntidade)
                .toList();

        return ResponseEntity.ok(new DashboardResponse(faturamento, totalPedidos, estoqueBaixo, ultimosPedidos));
    }
}
