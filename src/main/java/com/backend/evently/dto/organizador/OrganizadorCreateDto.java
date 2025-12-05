package com.backend.evently.dto.organizador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrganizadorCreateDto(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        String descricao,

        @Email(message = "E-mail de contato inválido")
        @NotBlank(message = "O e-mail de contato é obrigatório")
        String emailContato,

        @NotBlank(message = "O telefone de contato é obrigatório")
        String telefoneContato,

        String site,

        @NotNull(message = "O id do usuário é obrigatório")
        UUID idUsuario
) {}