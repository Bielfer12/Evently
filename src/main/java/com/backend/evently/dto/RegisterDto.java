package com.backend.evently.dto;

public record RegisterDto(
        String email,
        String password,
        String nome
) {}