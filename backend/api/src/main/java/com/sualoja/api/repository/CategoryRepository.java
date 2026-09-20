package com.sualoja.api.repository;

import com.sualoja.api.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNome(String nome);
    boolean existsByNome(String nome);
}