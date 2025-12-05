package com.backend.evently.dto.comentario;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ComentarioCreateDto(
        @NotNull(message = "O ID do evento é obrigatório")
        UUID idEvento,

        UUID idComentarioPai,

        @NotBlank(message = "O conteúdo não pode estar vazio")
        String conteudo,

        @Min(value = 1, message = "Avaliação mínima é 1")
        @Max(value = 5, message = "Avaliação máxima é 5")
        Short avaliacao
) {}