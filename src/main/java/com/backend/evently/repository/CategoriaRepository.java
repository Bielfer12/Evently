package com.backend.evently.repository;

import com.backend.evently.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    boolean existsByNome(String nome);
    boolean existsBySlug(String slug);
}