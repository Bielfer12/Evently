package com.backend.evently.dto.ingresso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record IngressoCreateDto(
        @NotBlank(message = "O nome do ingresso é obrigatório")
        String nome,

        @NotNull(message = "O ID do evento é obrigatório")
        UUID idEvento
) {}