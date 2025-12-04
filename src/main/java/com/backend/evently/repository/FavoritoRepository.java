package com.backend.evently.repository;

import com.backend.evently.model.Favorito;
import com.backend.evently.model.FavoritoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    // Busca paginada dos favoritos de um usuário específico
    Page<Favorito> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    // Verifica se já existe o favorito
    boolean existsById(FavoritoId id);
}