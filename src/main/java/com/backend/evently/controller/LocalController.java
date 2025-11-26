package com.backend.evently.controller;

import com.backend.evently.dto.local.LocalCreateDto;
import com.backend.evently.dto.local.LocalResponseDto;
import com.backend.evently.dto.local.LocalUpdateDto;
import com.backend.evently.service.LocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locais")
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    @PostMapping
    public ResponseEntity<LocalResponseDto> create(@Valid @RequestBody LocalCreateDto dto) {
        LocalResponseDto created = localService.createLocal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalResponseDto> update(@PathVariable UUID id,
                                                   @Valid @RequestBody LocalUpdateDto dto) {
        LocalResponseDto updated = localService.updateLocal(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        localService.deleteLocal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<LocalResponseDto>> getAll(
            @RequestParam(defaultValue = "-1") Integer pagina,
            @RequestParam(defaultValue = "-1") Integer resultados,
            @RequestParam(required = false) List<String> ordenar,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado
    ) {
        Page<LocalResponseDto> page = localService.getAll(pagina, resultados, ordenar, nome, cidade, estado);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(localService.getLocalById(id));
    }
}