package com.sualoja.api.repository;

import com.sualoja.api.model.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProdutoId(Long produtoId);
    Optional<ProductVariant> findBySku(String sku);
    boolean existsBySku(String sku);

    // Conta quantas variações têm estoque menor que 5
    @Query("SELECT COUNT(v) FROM ProductVariant v WHERE v.estoque < 5")
    long contarEstoqueBaixo();
}