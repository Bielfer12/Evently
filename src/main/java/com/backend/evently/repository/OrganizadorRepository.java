package com.backend.evently.repository;

import com.backend.evently.model.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizadorRepository extends JpaRepository<Organizador, UUID> {
    Optional<Organizador> findByUsuarioId(UUID usuarioId);
}
