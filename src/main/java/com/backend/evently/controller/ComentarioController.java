package com.backend.evently.controller;

import com.backend.evently.dto.comentario.ComentarioCreateDto;
import com.backend.evently.dto.comentario.ComentarioResponseDto;
import com.backend.evently.dto.comentario.ComentarioUpdateDto;
import com.backend.evently.service.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDto> criar(@Valid @RequestBody ComentarioCreateDto dto) {
        ComentarioResponseDto created = comentarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<Page<ComentarioResponseDto>> listarPorEvento(
            @PathVariable UUID idEvento,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados,
            @RequestParam(defaultValue = "false") boolean apenasRaiz
    ) {
        return ResponseEntity.ok(comentarioService.listarPorEvento(idEvento, pagina, resultados, apenasRaiz));
    }

    @GetMapping("/{idComentarioPai}/respostas")
    public ResponseEntity<Page<ComentarioResponseDto>> listarRespostas(
            @PathVariable UUID idComentarioPai,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados
    ) {
        return ResponseEntity.ok(comentarioService.listarRespostas(idComentarioPai, pagina, resultados));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponseDto> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ComentarioUpdateDto dto
    ) {
        return ResponseEntity.ok(comentarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        comentarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}