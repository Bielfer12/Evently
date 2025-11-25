package com.backend.evently.controller;

import com.backend.evently.dto.AuthResponseDto;
import com.backend.evently.dto.LoginDto;
import com.backend.evently.dto.RegisterDto;
import com.backend.evently.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok(authService.register(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @GetMapping(value = "/exportacao-csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv() {
        String csv = authService.exportUsuariosToCsv();

        return ResponseEntity
                .ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"usuarios.csv\"")
                .body(csv);
    }
}
