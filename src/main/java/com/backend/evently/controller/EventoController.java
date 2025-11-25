package com.backend.evently.controller;

import com.backend.evently.dto.evento.EventoCreateDto;
import com.backend.evently.dto.evento.EventoResponseDto;
import com.backend.evently.dto.evento.EventoUpdateDto;
import com.backend.evently.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<EventoResponseDto> create(@Valid @RequestBody EventoCreateDto dto) {
        EventoResponseDto created = eventoService.createEvento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDto> update(@PathVariable UUID id,
                                                    @Valid @RequestBody EventoUpdateDto dto) {
        EventoResponseDto updated = eventoService.updateEvento(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventoService.deleteEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<EventoResponseDto>> getAll(
            @RequestParam(defaultValue = "-1") Integer pagina,
            @RequestParam(defaultValue = "-1") Integer resultados,
            @RequestParam(required = false) List<String> ordenar,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID idCategoria,
            @RequestParam(required = false) UUID idOrganizador
    ) {
        Page<EventoResponseDto> page = eventoService.getAll(pagina, resultados, ordenar, titulo, status, idCategoria, idOrganizador);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventoService.getEventoById(id));
    }

    @GetMapping(value = "/exportacao-csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv() {
        String csv = eventoService.exportEventosToCsv();

        return ResponseEntity
                .ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"eventos.csv\"")
                .body(csv);
    }
}