package com.backend.evently.dto.participacao;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ParticipacaoCreateDto(
        @NotNull(message = "O ID do ingresso é obrigatório")
        UUID idIngresso
) {}