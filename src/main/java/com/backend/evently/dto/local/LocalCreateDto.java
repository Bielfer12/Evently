package com.backend.evently.dto.local;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocalCreateDto(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O slug é obrigatório")
        String slug,

        String descricao,

        @NotBlank(message = "O endereço é obrigatório")
        String endereco,

        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        String estado,

        @NotBlank(message = "O país é obrigatório")
        String pais,

        @NotNull(message = "A capacidade é obrigatória")
        @Min(value = 1, message = "A capacidade deve ser de pelo menos 1 pessoa")
        Integer capacidade
) {}