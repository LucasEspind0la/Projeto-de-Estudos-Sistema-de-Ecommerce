package com.sualoja.api.repository;

import com.sualoja.api.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAtivoTrue();
    List<Product> findByCategoriaId(Long categoriaId);
}