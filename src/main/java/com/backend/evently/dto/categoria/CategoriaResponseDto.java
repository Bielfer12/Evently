package com.backend.evently.dto.categoria;

import java.util.UUID;

public record CategoriaResponseDto(
        UUID id,
        String nome,
        String slug
) {}