package com.backend.evently.dto.local;

import jakarta.validation.constraints.Min;

public record LocalUpdateDto(
        String nome,
        String slug,
        String descricao,
        String endereco,
        String cidade,
        String estado,
        String pais,
        @Min(value = 1, message = "A capacidade deve ser de pelo menos 1 pessoa")
        Integer capacidade
) {}