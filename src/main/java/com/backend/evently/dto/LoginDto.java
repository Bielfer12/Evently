package com.backend.evently.dto;

public record LoginDto(
        String email,
        String password
) {}