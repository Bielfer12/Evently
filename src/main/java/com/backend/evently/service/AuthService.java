package com.backend.evently.service;

import com.backend.evently.config.jwt.JwtService;
import com.backend.evently.dto.AuthResponseDto;
import com.backend.evently.dto.LoginDto;
import com.backend.evently.dto.RegisterDto;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterDto request) {
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email já registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setPapel("USUARIO");

        repository.save(usuario);

        String jwtToken = jwtService.generateToken(usuario);

        return new AuthResponseDto(jwtToken, jwtService.getExpirationTime());
    }

    public AuthResponseDto login(LoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        Usuario usuario = (Usuario) repository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        String jwtToken = jwtService.generateToken(usuario);

        return new AuthResponseDto(jwtToken, jwtService.getExpirationTime());
    }
}