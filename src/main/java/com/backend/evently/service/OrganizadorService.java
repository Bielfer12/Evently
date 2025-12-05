package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.organizador.OrganizadorCreateDto;
import com.backend.evently.dto.organizador.OrganizadorResponseDto;
import com.backend.evently.dto.organizador.OrganizadorUpdateDto;
import com.backend.evently.exception.ConflictException;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Organizador;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.EventoRepository;
import com.backend.evently.repository.OrganizadorRepository;
import com.backend.evently.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizadorService {

    private final OrganizadorRepository organizadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;
    private final EventoRepository eventoRepository;

    @Transactional
    public OrganizadorResponseDto createOrganizador(OrganizadorCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        if (!isAdmin(usuarioLogado)) {
            throw new ForbiddenException("Apenas administradores podem criar organizadores");
        }

        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        organizadorRepository.findByUsuarioId(usuario.getId()).ifPresent(o -> {
            throw new ConflictException("Este usuário já está vinculado a um organizador");
        });

        Organizador organizador = new Organizador();
        organizador.setNome(dto.nome());
        organizador.setDescricao(dto.descricao());
        organizador.setEmailContato(dto.emailContato());
        organizador.setTelefoneContato(dto.telefoneContato());
        organizador.setSite(dto.site());
        organizador.setUsuario(usuario);

        return toResponseDto(organizadorRepository.save(organizador));
    }

    @Transactional(readOnly = true)
    public Page<OrganizadorResponseDto> getAll(Integer pagina, Integer resultados, String busca) {

        Sort sort = Sort.by("nome").ascending();
        List<Organizador> lista = organizadorRepository.findAll(sort);

        if (busca != null && !busca.isBlank()) {
            String b = busca.toLowerCase();
            lista = lista.stream()
                    .filter(o -> o.getNome() != null && o.getNome().toLowerCase().contains(b))
                    .toList();
        }

        int total = lista.size();
        int pageNum = (pagina != null && pagina >= 0) ? pagina : 0;
        int pageSize = (resultados != null && resultados > 0) ? resultados : 10;

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        int start = (int) pageable.getOffset();

        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        int end = Math.min(start + pageSize, total);

        List<OrganizadorResponseDto> content = lista.subList(start, end).stream()
                .map(this::toResponseDto)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    @Transactional(readOnly = true)
    public OrganizadorResponseDto getById(UUID id) {
        Organizador organizador = organizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado"));
        return toResponseDto(organizador);
    }

    @Transactional(readOnly = true)
    public OrganizadorResponseDto getMe() {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        Organizador organizador = organizadorRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum organizador vinculado a este usuário"));

        return toResponseDto(organizador);
    }

    @Transactional
    public OrganizadorResponseDto updateOrganizador(UUID id, OrganizadorUpdateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        if (!isAdmin(usuarioLogado)) {
            throw new ForbiddenException("Apenas administradores podem atualizar organizadores");
        }

        Organizador organizador = organizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado"));

        if (dto.nome() != null) {
            organizador.setNome(dto.nome());
        }
        if (dto.descricao() != null) {
            organizador.setDescricao(dto.descricao());
        }
        if (dto.emailContato() != null) {
            organizador.setEmailContato(dto.emailContato());
        }
        if (dto.telefoneContato() != null) {
            organizador.setTelefoneContato(dto.telefoneContato());
        }
        if (dto.site() != null) {
            organizador.setSite(dto.site());
        }

        return toResponseDto(organizadorRepository.save(organizador));
    }

    @Transactional
    public void deleteOrganizador(UUID id) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        if (!isAdmin(usuarioLogado)) {
            throw new ForbiddenException("Apenas administradores podem excluir organizadores");
        }

        Organizador organizador = organizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado"));

        long totalEventos = eventoRepository.countByOrganizadorId(id);
        if (totalEventos > 0) {
            throw new ConflictException(
                    "Não é possível excluir este organizador pois ele possui " + totalEventos + " evento(s) vinculado(s)"
            );
        }

        organizadorRepository.delete(organizador);
    }

    private boolean isAdmin(Usuario usuario) {
        return "admin".equalsIgnoreCase(usuario.getPapel());
    }

    private OrganizadorResponseDto toResponseDto(Organizador o) {
        return new OrganizadorResponseDto(
                o.getId(),
                o.getNome(),
                o.getDescricao(),
                o.getEmailContato(),
                o.getTelefoneContato(),
                o.getSite(),
                o.getUsuario() != null ? o.getUsuario().getId() : null,
                o.getCriadoEm(),
                o.getAtualizadoEm()
        );
    }
}