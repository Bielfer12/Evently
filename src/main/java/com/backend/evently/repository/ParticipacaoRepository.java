package com.backend.evently.repository;

import com.backend.evently.model.Participacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipacaoRepository extends JpaRepository<Participacao, UUID> {

    // Busca participações de um usuário (para a tela "Meus Ingressos")
    Page<Participacao> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    // Busca participações de um evento (para o Organizador ver a lista de presença)
    Page<Participacao> findAllByEventoId(UUID eventoId, Pageable pageable);
}