package com.backend.evently.service;

import com.backend.evently.config.jwt.JwtService;
import com.backend.evently.dto.AuthResponseDto;
import com.backend.evently.dto.LoginDto;
import com.backend.evently.dto.RegisterDto;
import com.backend.evently.exception.ConflictException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterDto request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email já registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setPapel("usuario");

        usuarioRepository.save(usuario);

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

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        String jwtToken = jwtService.generateToken(usuario);

        return new AuthResponseDto(jwtToken, jwtService.getExpirationTime());
    }

    public String exportUsuariosToCsv() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        StringBuilder sb = new StringBuilder();

        sb.append("id;nome;email;papel;criadoEm;atualizadoEm\n");

        for (Usuario usuario : usuarios) {
            sb.append(safe(usuario.getId()))
                    .append(';')
                    .append(escape(usuario.getNome()))
                    .append(';')
                    .append(escape(usuario.getEmail()))
                    .append(';')
                    .append(usuario.getPapel())
                    .append(';')
                    .append(usuario.getCriadoEm())
                    .append(';')
                    .append(usuario.getAtualizadoEm() != null ? usuario.getAtualizadoEm() : "")
                    .append('\n');
        }

        return sb.toString();
    }

    private String safe(Object value) {
        return value != null ? value.toString() : "";
    }

    private String escape(String value) {
        if (value == null) return "";
        String v = value.replace(";", ",");
        if (v.contains("\"") || v.contains(",") || v.contains(";") || v.contains("\n")) {
            v = v.replace("\"", "\"\"");
            return "\"" + v + "\"";
        }
        return v;
    }
}