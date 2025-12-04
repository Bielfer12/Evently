package com.backend.evently.controller;

import com.backend.evently.dto.categoria.CategoriaCreateDto;
import com.backend.evently.dto.categoria.CategoriaResponseDto;
import com.backend.evently.dto.categoria.CategoriaUpdateDto;
import com.backend.evently.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDto> create(@Valid @RequestBody CategoriaCreateDto dto) {
        CategoriaResponseDto created = categoriaService.createCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> update(@PathVariable UUID id,
                                                       @Valid @RequestBody CategoriaUpdateDto dto) {
        CategoriaResponseDto updated = categoriaService.updateCategoria(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDto>> getAll(
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "10") Integer resultados,
            @RequestParam(required = false) String busca
    ) {
        Page<CategoriaResponseDto> page = categoriaService.getAll(pagina, resultados, busca);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(id));
    }
}