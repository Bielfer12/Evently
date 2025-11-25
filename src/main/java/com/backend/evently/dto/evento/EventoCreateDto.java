package com.backend.evently.dto.evento;

import com.backend.evently.enums.StatusEventoEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EventoCreateDto(
        @NotBlank(message = "titulo é obrigatório") String titulo,
        @NotBlank(message = "slug é obrigatório") String slug,
        String descricaoCurta,
        String descricao,
        @NotNull(message = "status é obrigatório") StatusEventoEnum status,
        @Min(value = 1, message = "capacidade deve ser >= 1") Integer capacidade,
        String metadados,
        @NotNull(message = "local é obrigatório") UUID idLocal,
        @NotNull(message = "categoria é obrigatória") UUID idCategoria,
        @NotNull(message = "organizador é obrigatório") UUID idOrganizador
) {}
