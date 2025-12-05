package com.backend.evently.dto.participacao;

import java.time.Instant;
import java.util.UUID;

public record ParticipacaoResponseDto(
        UUID idParticipacao,
        UUID idEvento,
        String tituloEvento,
        UUID idIngresso,
        String nomeIngresso,
        String nomeParticipante, // Útil para o organizador ver quem comprou
        Instant dataInscricao
) {}