package com.backend.evently.dto.categoria;

// Campos opcionais, pois posso querer atualizar só o nome e manter o slug
public record CategoriaUpdateDto(
        String nome,
        String slug
) {}