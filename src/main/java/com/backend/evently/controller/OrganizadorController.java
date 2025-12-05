package com.backend.evently.controller;

import com.backend.evently.dto.organizador.OrganizadorCreateDto;
import com.backend.evently.dto.organizador.OrganizadorResponseDto;
import com.backend.evently.dto.organizador.OrganizadorUpdateDto;
import com.backend.evently.service.OrganizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizadores")
@RequiredArgsConstructor
public class OrganizadorController {

    private final OrganizadorService organizadorService;

    @PostMapping
    public ResponseEntity<OrganizadorResponseDto> create(@Valid @RequestBody OrganizadorCreateDto dto) {
        OrganizadorResponseDto created = organizadorService.createOrganizador(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<OrganizadorResponseDto>> getAll(
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados,
            @RequestParam(required = false) String busca
    ) {
        Page<OrganizadorResponseDto> page = organizadorService.getAll(pagina, resultados, busca);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizadorResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(organizadorService.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<OrganizadorResponseDto> getMe() {
        return ResponseEntity.ok(organizadorService.getMe());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizadorResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody OrganizadorUpdateDto dto
    ) {
        return ResponseEntity.ok(organizadorService.updateOrganizador(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        organizadorService.deleteOrganizador(id);
        return ResponseEntity.noContent().build();
    }
}