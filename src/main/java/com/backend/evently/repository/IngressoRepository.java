package com.backend.evently.repository;

import com.backend.evently.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IngressoRepository extends JpaRepository<Ingresso, UUID> {
    // Busca todos os ingressos vinculados a um evento
    List<Ingresso> findByEventoId(UUID idEvento);
}