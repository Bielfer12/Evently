package com.backend.evently.controller;

import com.backend.evently.dto.evento.EventoResponseDto;
import com.backend.evently.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @PostMapping("/{idEvento}")
    public ResponseEntity<Void> adicionarFavorito(@PathVariable UUID idEvento) {
        favoritoService.adicionarFavorito(idEvento);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{idEvento}")
    public ResponseEntity<Void> removerFavorito(@PathVariable UUID idEvento) {
        favoritoService.removerFavorito(idEvento);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idEvento}/check")
    public ResponseEntity<Boolean> isFavorito(@PathVariable UUID idEvento) {
        boolean isFav = favoritoService.isFavorito(idEvento);
        return ResponseEntity.ok(isFav);
    }

    @GetMapping
    public ResponseEntity<Page<EventoResponseDto>> listarMeusFavoritos(
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados
    ) {
        Page<EventoResponseDto> page = favoritoService.listarFavoritosDoUsuario(pagina, resultados);
        return ResponseEntity.ok(page);
    }
}