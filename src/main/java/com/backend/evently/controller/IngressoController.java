package com.backend.evently.controller;

import com.backend.evently.dto.ingresso.IngressoCreateDto;
import com.backend.evently.dto.ingresso.IngressoResponseDto;
import com.backend.evently.dto.ingresso.IngressoUpdateDto;
import com.backend.evently.service.IngressoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ingressos")
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoService ingressoService;

    @PostMapping
    public ResponseEntity<IngressoResponseDto> create(@Valid @RequestBody IngressoCreateDto dto) {
        IngressoResponseDto created = ingressoService.createIngresso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<IngressoResponseDto>> listByEvento(@PathVariable UUID idEvento) {
        return ResponseEntity.ok(ingressoService.listByEvento(idEvento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngressoResponseDto> update(@PathVariable UUID id,
                                                      @Valid @RequestBody IngressoUpdateDto dto) {
        return ResponseEntity.ok(ingressoService.updateIngresso(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ingressoService.deleteIngresso(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngressoResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ingressoService.getById(id));
    }
}