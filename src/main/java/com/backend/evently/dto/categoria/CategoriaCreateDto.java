package com.backend.evently.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaCreateDto(
        @NotBlank(message = "O nome é obrigatório") String nome,
        @NotBlank(message = "O slug é obrigatório") String slug
) {}