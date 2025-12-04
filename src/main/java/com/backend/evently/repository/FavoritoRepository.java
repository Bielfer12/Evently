package com.backend.evently.repository;

import com.backend.evently.model.Favorito;
import com.backend.evently.model.FavoritoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    Page<Favorito> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    boolean existsById(FavoritoId id);
}