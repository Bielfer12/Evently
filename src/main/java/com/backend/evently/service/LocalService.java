package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.local.LocalCreateDto;
import com.backend.evently.dto.local.LocalResponseDto;
import com.backend.evently.dto.local.LocalUpdateDto;
import com.backend.evently.exception.ConflictException;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Local;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalService {

    private final LocalRepository localRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public LocalResponseDto createLocal(LocalCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        boolean isOrganizadorOuAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel())
                || "organizador".equalsIgnoreCase(usuarioLogado.getPapel());

        if (!isOrganizadorOuAdmin) {
            throw new ForbiddenException("Você não tem permissão para criar locais.");
        }

        if (localRepository.existsBySlug(dto.slug())) {
            throw new ConflictException("Já existe um local com este slug");
        }

        Local local = new Local();
        local.setNome(dto.nome());
        local.setSlug(dto.slug());
        local.setDescricao(dto.descricao());
        local.setEndereco(dto.endereco());
        local.setCidade(dto.cidade());
        local.setEstado(dto.estado());
        local.setPais(dto.pais());
        local.setCapacidade(dto.capacidade());

        local.setCriadoPor(usuarioLogado.getNome());

        Local salvo = localRepository.save(local);
        return toResponseDto(salvo);
    }

    @Transactional
    public LocalResponseDto updateLocal(UUID localId, LocalUpdateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Local local = localRepository.findById(localId)
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado"));

        if (!isAdmin) {
            String nomeCriador = local.getCriadoPor();
            if (nomeCriador == null || !nomeCriador.equals(usuarioLogado.getNome())) {
                throw new ForbiddenException("Você não tem permissão para editar este local.");
            }
        }

        if (dto.slug() != null && !dto.slug().equals(local.getSlug())) {
            if (localRepository.existsBySlug(dto.slug())) {
                throw new ConflictException("Já existe um local com este slug");
            }
        }

        if (dto.nome() != null) local.setNome(dto.nome());
        if (dto.slug() != null) local.setSlug(dto.slug());
        if (dto.descricao() != null) local.setDescricao(dto.descricao());
        if (dto.endereco() != null) local.setEndereco(dto.endereco());
        if (dto.cidade() != null) local.setCidade(dto.cidade());
        if (dto.estado() != null) local.setEstado(dto.estado());
        if (dto.pais() != null) local.setPais(dto.pais());
        if (dto.capacidade() != null) local.setCapacidade(dto.capacidade());

        Local salvo = localRepository.save(local);
        return toResponseDto(salvo);
    }

    @Transactional
    public void deleteLocal(UUID localId) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Local local = localRepository.findById(localId)
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado"));

        if (!isAdmin) {
            String nomeCriador = local.getCriadoPor();
            if (nomeCriador == null || !nomeCriador.equals(usuarioLogado.getNome())) {
                throw new ForbiddenException("Você não tem permissão para remover este local.");
            }
        }

        localRepository.delete(local);
    }

    public Page<LocalResponseDto> getAll(
            Integer pagina,
            Integer resultados,
            List<String> ordenar,
            String nome,
            String cidade,
            String estado
    ) {
        List<Local> locais = localRepository.findAll();

        if (nome != null && !nome.isBlank()) {
            String nomeLower = nome.toLowerCase();
            locais = locais.stream()
                    .filter(l -> l.getNome() != null &&
                            l.getNome().toLowerCase().contains(nomeLower))
                    .collect(Collectors.toList());
        }

        if (cidade != null && !cidade.isBlank()) {
            String cidadeLower = cidade.toLowerCase();
            locais = locais.stream()
                    .filter(l -> l.getCidade() != null &&
                            l.getCidade().toLowerCase().contains(cidadeLower))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.isBlank()) {
            String estadoLower = estado.toLowerCase();
            locais = locais.stream()
                    .filter(l -> l.getEstado() != null &&
                            l.getEstado().toLowerCase().contains(estadoLower))
                    .collect(Collectors.toList());
        }

        Sort sort = parseSort(ordenar);
        if (sort.isSorted()) {
            locais.sort(createComparator(sort));
        }

        int total = locais.size();

        if (pagina == null || resultados == null || pagina < 0 || resultados <= 0) {
            List<LocalResponseDto> dtos = locais.stream()
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

        List<LocalResponseDto> pageContent = locais.subList(start, end).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, total);
    }

    public LocalResponseDto getLocalById(UUID localId) {
        Local local = localRepository.findById(localId)
                .orElseThrow(() -> new ResourceNotFoundException("Local não encontrado"));
        return toResponseDto(local);
    }

    @SuppressWarnings({"rawtypes"})
    private Comparator<Local> createComparator(Sort sort) {
        return (l1, l2) -> {
            for (Sort.Order order : sort) {
                Comparable v1 = getSortableField(l1, order.getProperty());
                Comparable v2 = getSortableField(l2, order.getProperty());

                int result = compare(v1, v2);
                if (result != 0) {
                    return order.getDirection().isAscending() ? result : -result;
                }
            }
            return 0;
        };
    }

    @SuppressWarnings({"rawtypes"})
    private Comparable getSortableField(Local l, String property) {
        return switch (property) {
            case "nome" -> l.getNome();
            case "cidade" -> l.getCidade();
            case "estado" -> l.getEstado();
            case "capacidade" -> l.getCapacidade();
            case "criadoEm", "criado_em" -> l.getCriadoEm();
            case "atualizadoEm", "atualizado_em" -> l.getAtualizadoEm();
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
            return Sort.by("nome").ascending();
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

        return orders.isEmpty() ? Sort.by("nome").ascending() : Sort.by(orders);
    }

    private LocalResponseDto toResponseDto(Local local) {
        return new LocalResponseDto(
                local.getId(),
                local.getNome(),
                local.getSlug(),
                local.getDescricao(),
                local.getEndereco(),
                local.getCidade(),
                local.getEstado(),
                local.getPais(),
                local.getCapacidade(),
                local.getCriadoPor(),
                local.getCriadoEm(),
                local.getAtualizadoEm()
        );
    }
}