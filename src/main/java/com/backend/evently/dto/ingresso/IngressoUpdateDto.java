package com.backend.evently.dto.ingresso;

import jakarta.validation.constraints.NotBlank;

public record IngressoUpdateDto(
        @NotBlank(message = "O nome do ingresso é obrigatório")
        String nome
) {}