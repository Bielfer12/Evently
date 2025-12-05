package com.backend.evently.repository;

import com.backend.evently.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {

    Page<Comentario> findAllByEventoId(UUID eventoId, Pageable pageable);

    Page<Comentario> findAllByEventoIdAndComentarioPaiIsNull(UUID eventoId, Pageable pageable);

    Page<Comentario> findAllByComentarioPaiId(UUID comentarioPaiId, Pageable pageable);

    void deleteAllByComentarioPaiId(UUID comentarioPaiId);
}