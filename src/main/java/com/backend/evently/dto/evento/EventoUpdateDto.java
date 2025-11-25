package com.backend.evently.dto.evento;

import com.backend.evently.enums.StatusEventoEnum;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record EventoUpdateDto(
        String titulo,
        String slug,
        String descricaoCurta,
        String descricao,
        StatusEventoEnum status,
        @Min(value = 1, message = "capacidade deve ser >= 1") Integer capacidade,
        String metadados,
        UUID idLocal,
        UUID idCategoria
) {}