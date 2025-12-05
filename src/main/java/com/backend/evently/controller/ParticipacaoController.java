package com.backend.evently.controller;

import com.backend.evently.dto.participacao.ParticipacaoCreateDto;
import com.backend.evently.dto.participacao.ParticipacaoResponseDto;
import com.backend.evently.service.ParticipacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/participacoes")
@RequiredArgsConstructor
public class ParticipacaoController {

    private final ParticipacaoService participacaoService;

    @PostMapping
    public ResponseEntity<ParticipacaoResponseDto> registrar(@Valid @RequestBody ParticipacaoCreateDto dto) {
        ParticipacaoResponseDto created = participacaoService.registrarParticipacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/minhas")
    public ResponseEntity<Page<ParticipacaoResponseDto>> minhasParticipacoes(
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados
    ) {
        return ResponseEntity.ok(participacaoService.listarMinhasParticipacoes(pagina, resultados));
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<Page<ParticipacaoResponseDto>> listarPorEvento(
            @PathVariable UUID idEvento,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados
    ) {
        return ResponseEntity.ok(participacaoService.listarParticipantesPorEvento(idEvento, pagina, resultados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        participacaoService.cancelarParticipacao(id);
        return ResponseEntity.noContent().build();
    }
}