package com.backend.evently.dto.organizador;

import jakarta.validation.constraints.Email;

public record OrganizadorUpdateDto(
        String nome,
        String descricao,
        @Email(message = "E-mail de contato inválido")
        String emailContato,
        String telefoneContato,
        String site
) {}