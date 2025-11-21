package com.backend.evently.dto.evento;

import com.backend.evently.enums.StatusEventoEnum;

import java.util.UUID;

public record EventoCreateDto(
        String titulo,
        String slug,
        String descricaoCurta,
        String descricao,
        StatusEventoEnum status,
        Integer capacidade,
        String metadados,
        UUID idOrganizador,
        UUID idLocal,
        UUID idCategoria
) {}
