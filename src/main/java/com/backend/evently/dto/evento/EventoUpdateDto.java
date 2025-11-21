package com.backend.evently.dto.evento;

import com.backend.evently.enums.StatusEventoEnum;

import java.util.UUID;

public record EventoUpdateDto(
        String titulo,
        String slug,
        String descricaoCurta,
        String descricao,
        StatusEventoEnum status,
        Integer capacidade,
        String metadados,
        UUID idLocal,
        UUID idCategoria
) {}