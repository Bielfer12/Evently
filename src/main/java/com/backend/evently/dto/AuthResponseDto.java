package com.backend.evently.dto;

public record AuthResponseDto(
        String token,
        Long expiresIn
) {}