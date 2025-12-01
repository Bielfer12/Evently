package com.backend.evently.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @Email
        String email,
        @Size(min = 8, message = "senha deve possuir no mínimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
                message = "senha deve conter pelo menos 1 letra, 1 número e 1 caractere especial"
        )
        String password,
        @NotBlank(message = "nome é obrigatório") String nome
) {}