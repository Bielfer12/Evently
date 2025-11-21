package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.evento.EventoCreateDto;
import com.backend.evently.dto.evento.EventoResponseDto;
import com.backend.evently.dto.evento.EventoUpdateDto;
import com.backend.evently.model.Categoria;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Local;
import com.backend.evently.model.Organizador;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.CategoriaRepository;
import com.backend.evently.repository.EventoRepository;
import com.backend.evently.repository.LocalRepository;
import com.backend.evently.repository.OrganizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final OrganizadorRepository organizadorRepository;
    private final LocalRepository localRepository;
    private final CategoriaRepository categoriaRepository;
    private final CurrentUserService currentUserService;

    public EventoResponseDto createEvento(EventoCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        if (eventoRepository.existsBySlug(dto.slug())) {
            throw new RuntimeException("Já existe um evento com este slug");
        }

        Evento evento = new Evento();
        evento.setTitulo(dto.titulo());
        evento.setSlug(dto.slug());
        evento.setDescricao_curta(dto.descricaoCurta());
        evento.setDescricao(dto.descricao());
        evento.setStatusEventoEnum(dto.status());
        evento.setCapacidade(dto.capacidade());
        evento.setMetadados(dto.metadados());

        if (isAdmin) {
            if (dto.idOrganizador() != null) {
                Organizador organizador = organizadorRepository.findById(dto.idOrganizador())
                        .orElseThrow(() -> new RuntimeException("Organizador não encontrado"));
                evento.setOrganizador(organizador);
            }
        } else {
            if (usuarioLogado.getOrganizador() == null) {
                throw new RuntimeException("Apenas organizadores podem criar eventos");
            }
            evento.setOrganizador(usuarioLogado.getOrganizador());
        }

        if (dto.idLocal() != null) {
            Local local = localRepository.findById(dto.idLocal())
                    .orElseThrow(() -> new RuntimeException("Local não encontrado"));
            evento.setLocal(local);
        }

        if (dto.idCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            evento.setCategoria(categoria);
        }

        Evento salvo = eventoRepository.save(evento);
        return toResponseDto(salvo);
    }

    public EventoResponseDto updateEvento(UUID eventoId, EventoUpdateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        if (!isAdmin) {
            if (usuarioLogado.getOrganizador() == null) {
                throw new RuntimeException("Apenas organizadores podem editar eventos");
            }

            UUID organizadorDoEvento = evento.getOrganizador() != null
                    ? evento.getOrganizador().getId()
                    : null;
            UUID organizadorDoUsuario = usuarioLogado.getOrganizador().getId();

            if (organizadorDoEvento == null || !organizadorDoEvento.equals(organizadorDoUsuario)) {
                throw new RuntimeException("Você não tem permissão para editar este evento");
            }
        }

        if (dto.slug() != null && !dto.slug().equals(evento.getSlug())) {
            if (eventoRepository.existsBySlug(dto.slug())) {
                throw new RuntimeException("Já existe um evento com este slug");
            }
        }

        if (dto.titulo() != null) evento.setTitulo(dto.titulo());
        if (dto.slug() != null) evento.setSlug(dto.slug());
        if (dto.descricaoCurta() != null) evento.setDescricao_curta(dto.descricaoCurta());
        if (dto.descricao() != null) evento.setDescricao(dto.descricao());
        if (dto.status() != null) evento.setStatusEventoEnum(dto.status());
        if (dto.capacidade() != null) evento.setCapacidade(dto.capacidade());
        if (dto.metadados() != null) evento.setMetadados(dto.metadados());

        if (dto.idLocal() != null) {
            Local local = localRepository.findById(dto.idLocal())
                    .orElseThrow(() -> new RuntimeException("Local não encontrado"));
            evento.setLocal(local);
        }

        if (dto.idCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            evento.setCategoria(categoria);
        }

        Evento salvo = eventoRepository.save(evento);
        return toResponseDto(salvo);
    }

    public void deleteEvento(UUID eventoId) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        if (!isAdmin) {
            if (usuarioLogado.getOrganizador() == null) {
                throw new RuntimeException("Apenas organizadores podem remover eventos");
            }

            UUID organizadorDoEvento = evento.getOrganizador() != null
                    ? evento.getOrganizador().getId()
                    : null;
            UUID organizadorDoUsuario = usuarioLogado.getOrganizador().getId();

            if (organizadorDoEvento == null || !organizadorDoEvento.equals(organizadorDoUsuario)) {
                throw new RuntimeException("Você não tem permissão para remover este evento");
            }
        }

        eventoRepository.delete(evento);
    }

    public List<EventoResponseDto> getAll() {
        return eventoRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public EventoResponseDto getEventoById(UUID eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        return toResponseDto(evento);
    }

    private EventoResponseDto toResponseDto(Evento evento) {
        return new EventoResponseDto(
                evento.getId(),
                evento.getTitulo(),
                evento.getSlug(),
                evento.getDescricao_curta(),
                evento.getDescricao(),
                evento.getStatusEventoEnum(),
                evento.getCapacidade(),
                evento.getMetadados(),
                evento.getOrganizador() != null ? evento.getOrganizador().getId() : null,
                evento.getLocal() != null ? evento.getLocal().getId() : null,
                evento.getCategoria() != null ? evento.getCategoria().getId() : null
        );
    }
}