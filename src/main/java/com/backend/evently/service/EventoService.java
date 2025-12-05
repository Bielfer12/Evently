package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.evento.EventoCreateDto;
import com.backend.evently.dto.evento.EventoResponseDto;
import com.backend.evently.dto.evento.EventoUpdateDto;
import com.backend.evently.enums.StatusEventoEnum;
import com.backend.evently.exception.ConflictException;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Categoria;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Local;
import com.backend.evently.model.Organizador;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.CategoriaRepository;
import com.backend.evently.repository.EventoRepository;
import com.backend.evently.repository.LocalRepository;
import com.backend.evently.repository.OrganizadorRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final OrganizadorRepository organizadorRepository;
    private final LocalRepository localRepository;
    private final CategoriaRepository categoriaRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public EventoResponseDto createEvento(EventoCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        if (eventoRepository.existsBySlug(dto.slug())) {
            throw new ConflictException("Já existe um evento com este slug");
        }

        Evento evento = new Evento();
        evento.setTitulo(dto.titulo());
        evento.setSlug(dto.slug());
        evento.setDescricao_curta(dto.descricaoCurta());
        evento.setDescricao(dto.descricao());
        evento.setStatusEventoEnum(dto.status());
        evento.setCapacidade(dto.capacidade());
        evento.setMetadados(dto.metadados());
        evento.setCriadoPor(usuarioLogado.getNome());

        if (isAdmin) {
            Organizador organizador = organizadorRepository.findById(dto.idOrganizador())
                    .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado"));
            evento.setOrganizador(organizador);
        } else {
            Organizador organizador = organizadorRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ForbiddenException("Apenas organizadores podem criar eventos"));
            evento.setOrganizador(organizador);
        }

        Local local = localRepository.findById(dto.idLocal())
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado"));
        evento.setLocal(local);

        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        evento.setCategoria(categoria);

        Evento salvo = eventoRepository.save(evento);
        return toResponseDto(salvo);
    }

    @Transactional
    public EventoResponseDto updateEvento(UUID eventoId, EventoUpdateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        if (!isAdmin) {
            Organizador organizadorDoUsuario = organizadorRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ForbiddenException("Apenas organizadores podem editar eventos"));

            UUID organizadorDoEvento = evento.getOrganizador() != null
                    ? evento.getOrganizador().getId()
                    : null;

            if (organizadorDoEvento == null || !organizadorDoEvento.equals(organizadorDoUsuario.getId())) {
                throw new ForbiddenException("Você não tem permissão para editar este evento");
            }
        }

        if (dto.slug() != null && !dto.slug().equals(evento.getSlug())) {
            if (eventoRepository.existsBySlug(dto.slug())) {
                throw new ConflictException("Já existe um evento com este slug");
            }
        }

        if (dto.titulo() != null) evento.setTitulo(dto.titulo());
        if (dto.slug() != null) evento.setSlug(dto.slug());
        if (dto.descricaoCurta() != null) evento.setDescricao_curta(dto.descricaoCurta());
        if (dto.descricao() != null) evento.setDescricao(dto.descricao());
        if (dto.status() != null) evento.setStatusEventoEnum(dto.status());
        if (dto.capacidade() != null) evento.setCapacidade(dto.capacidade());
        if (dto.metadados() != null) evento.setMetadados(dto.metadados());

        Local local = localRepository.findById(dto.idLocal())
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado"));
        evento.setLocal(local);

        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        evento.setCategoria(categoria);

        Evento salvo = eventoRepository.save(evento);
        return toResponseDto(salvo);
    }

    @Transactional
    public void deleteEvento(UUID eventoId) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        if (!isAdmin) {
            Organizador organizadorDoUsuario = organizadorRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ForbiddenException("Apenas organizadores podem remover eventos"));

            UUID organizadorDoEvento = evento.getOrganizador() != null
                    ? evento.getOrganizador().getId()
                    : null;

            if (organizadorDoEvento == null || !organizadorDoEvento.equals(organizadorDoUsuario.getId())) {
                throw new ForbiddenException("Você não tem permissão para remover este evento");
            }
        }

        eventoRepository.delete(evento);
    }

    public Page<EventoResponseDto> getAll(
            Integer pagina,
            Integer resultados,
            List<String> ordenar,
            String titulo,
            String statusStr,
            UUID idCategoria,
            UUID idOrganizador
    ) {
        StatusEventoEnum status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = StatusEventoEnum.valueOf(statusStr.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return new PageImpl<>(List.of());
            }
        }

        List<Evento> eventos = eventoRepository.findAll();

        if (titulo != null && !titulo.isBlank()) {
            String tituloLower = titulo.toLowerCase();
            eventos = eventos.stream()
                    .filter(e -> e.getTitulo() != null &&
                            e.getTitulo().toLowerCase().contains(tituloLower))
                    .collect(Collectors.toList());
        }

        if (status != null) {
            StatusEventoEnum finalStatus = status;
            eventos = eventos.stream()
                    .filter(e -> finalStatus.equals(e.getStatusEventoEnum()))
                    .collect(Collectors.toList());
        }

        if (idCategoria != null) {
            eventos = eventos.stream()
                    .filter(e -> e.getCategoria() != null &&
                            idCategoria.equals(e.getCategoria().getId()))
                    .collect(Collectors.toList());
        }

        if (idOrganizador != null) {
            eventos = eventos.stream()
                    .filter(e -> e.getOrganizador() != null &&
                            idOrganizador.equals(e.getOrganizador().getId()))
                    .collect(Collectors.toList());
        }

        Sort sort = parseSort(ordenar);
        if (sort.isSorted()) {
            eventos.sort(createComparator(sort));
        }

        int total = eventos.size();

        if (pagina == null || resultados == null || pagina < 0 || resultados <= 0) {
            List<EventoResponseDto> dtos = eventos.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            return new PageImpl<>(dtos);
        }

        Pageable pageable = PageRequest.of(pagina, resultados);
        int start = (int) pageable.getOffset();
        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }
        int end = Math.min(start + pageable.getPageSize(), total);

        List<EventoResponseDto> pageContent = eventos.subList(start, end).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, total);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Comparator<Evento> createComparator(Sort sort) {
        return (e1, e2) -> {
            for (Sort.Order order : sort) {
                Comparable v1 = getSortableField(e1, order.getProperty());
                Comparable v2 = getSortableField(e2, order.getProperty());

                int result = compare(v1, v2);
                if (result != 0) {
                    return order.getDirection().isAscending() ? result : -result;
                }
            }
            return 0;
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Comparable getSortableField(Evento e, String property) {
        return switch (property) {
            case "titulo" -> e.getTitulo();
            case "criadoEm", "criado_em" -> e.getCriadoEm();
            case "atualizadoEm", "atualizado_em" -> e.getAtualizadoEm();
            case "status" -> e.getStatusEventoEnum() != null ? e.getStatusEventoEnum().name() : null;
            case "capacidade" -> e.getCapacidade();
            default -> null;
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compare(Comparable a, Comparable b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }

    private Sort parseSort(List<String> ordenar) {
        if (ordenar == null || ordenar.isEmpty()) {
            return Sort.by("titulo").ascending();
        }

        List<Sort.Order> orders = ordenar.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> {
                    String[] parts = s.split(",");
                    String prop = parts[0].trim();
                    Sort.Direction dir = Sort.Direction.ASC;
                    if (parts.length > 1) {
                        try {
                            dir = Sort.Direction.fromString(parts[1].trim());
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    return new Sort.Order(dir, prop);
                })
                .toList();

        return orders.isEmpty() ? Sort.by("titulo").ascending() : Sort.by(orders);
    }

    public EventoResponseDto getEventoById(UUID eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));
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

    public String exportEventosToCsv() {
        List<Evento> eventos = eventoRepository.findAll();

        StringBuilder sb = new StringBuilder();

        sb.append("id;titulo;slug;status;capacidade;organizador;local;categoria;criadoEm;criadoPor;atualizadoEm\n");

        for (Evento evento : eventos) {
            sb.append(safe(evento.getId()))
                    .append(';')
                    .append(escape(evento.getTitulo()))
                    .append(';')
                    .append(escape(evento.getSlug()))
                    .append(';')
                    .append(evento.getStatusEventoEnum())
                    .append(';')
                    .append(evento.getCapacidade())
                    .append(';')
                    .append(evento.getOrganizador() != null ? escape(evento.getOrganizador().getNome()) : "")
                    .append(';')
                    .append(evento.getLocal() != null ? escape(evento.getLocal().getNome()) : "")
                    .append(';')
                    .append(evento.getCategoria() != null ? escape(evento.getCategoria().getNome()) : "")
                    .append(';')
                    .append(evento.getCriadoEm())
                    .append(';')
                    .append(escape(evento.getCriadoPor()))
                    .append(';')
                    .append(evento.getAtualizadoEm() != null ? evento.getAtualizadoEm() : "")
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