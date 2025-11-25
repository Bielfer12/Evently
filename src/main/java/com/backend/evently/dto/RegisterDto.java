package com.backend.evently.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @Email
        String email,
        @Min(value = 8, message = "senha deve possuir no mínimo 8 caracteres")
        String password,
        @NotBlank(message = "nome é obrigatório") String nome
) {}