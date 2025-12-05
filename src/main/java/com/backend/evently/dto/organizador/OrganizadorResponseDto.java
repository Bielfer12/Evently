package com.backend.evently.dto.organizador;

import java.time.Instant;
import java.util.UUID;

public record OrganizadorResponseDto(
        UUID id,
        String nome,
        String descricao,
        String emailContato,
        String telefoneContato,
        String site,
        UUID idUsuario,
        Instant criadoEm,
        Instant atualizadoEm
) {}