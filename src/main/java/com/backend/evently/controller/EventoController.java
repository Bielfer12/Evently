package com.backend.evently.controller;

import com.backend.evently.dto.evento.EventoCreateDto;
import com.backend.evently.dto.evento.EventoResponseDto;
import com.backend.evently.dto.evento.EventoUpdateDto;
import com.backend.evently.service.EventoService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<EventoResponseDto> create(@RequestBody EventoCreateDto dto) {
        EventoResponseDto created = eventoService.createEvento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDto> update(@PathVariable UUID id,
                                                    @RequestBody EventoUpdateDto dto) {
        EventoResponseDto updated = eventoService.updateEvento(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventoService.deleteEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDto>> getAll() {
        return ResponseEntity.ok(eventoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventoService.getEventoById(id));
    }
}