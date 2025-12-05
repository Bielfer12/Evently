package com.backend.evently.repository;

import com.backend.evently.model.Participacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipacaoRepository extends JpaRepository<Participacao, UUID> {

    Page<Participacao> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    Page<Participacao> findAllByEventoId(UUID eventoId, Pageable pageable);
}