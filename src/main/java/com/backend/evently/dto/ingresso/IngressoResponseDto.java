package com.backend.evently.dto.ingresso;

import java.util.UUID;

public record IngressoResponseDto(
        UUID id,
        String nome,
        UUID idEvento
) {}