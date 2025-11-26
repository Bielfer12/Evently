package com.backend.evently.repository;

import com.backend.evently.model.Local;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocalRepository extends JpaRepository<Local, UUID> {
    boolean existsBySlug(@NotBlank(message = "O slug é obrigatório") String slug);
}