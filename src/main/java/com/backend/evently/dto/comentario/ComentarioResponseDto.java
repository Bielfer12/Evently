package com.backend.evently.dto.comentario;

import java.time.Instant;
import java.util.UUID;

public record ComentarioResponseDto(
        UUID id,
        UUID idEvento,
        String tituloEvento,
        UUID idUsuario,
        String nomeUsuario,
        UUID idComentarioPai,
        String conteudo,
        Short avaliacao,
        Instant criadoEm
) {}