package com.sualoja.api.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant varianteProduto;

    @Column(nullable = false)
    private Integer quantidade;

    // Guardamos o preço no momento da compra. 
    // Se o preço do produto mudar depois, o histórico do pedido não é afetado.
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;
}