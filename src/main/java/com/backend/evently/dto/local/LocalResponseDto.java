package com.backend.evently.dto.local;

import java.time.Instant;
import java.util.UUID;

public record LocalResponseDto(
        UUID id,
        String nome,
        String slug,
        String descricao,
        String endereco,
        String cidade,
        String estado,
        String pais,
        Integer capacidade,
        String criadoPor,
        Instant criadoEm,
        Instant atualizadoEm
) {}