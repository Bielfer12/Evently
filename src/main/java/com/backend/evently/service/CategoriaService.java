package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.categoria.CategoriaCreateDto;
import com.backend.evently.dto.categoria.CategoriaResponseDto;
import com.backend.evently.dto.categoria.CategoriaUpdateDto;
import com.backend.evently.exception.ConflictException;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Categoria;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public CategoriaResponseDto createCategoria(CategoriaCreateDto dto) {
        verificarPermissaoAdmin();

        if (categoriaRepository.existsByNome(dto.nome())) {
            throw new ConflictException("Já existe uma categoria com este nome");
        }
        if (categoriaRepository.existsBySlug(dto.slug())) {
            throw new ConflictException("Já existe uma categoria com este slug");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setSlug(dto.slug());

        return toResponseDto(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDto updateCategoria(UUID id, CategoriaUpdateDto dto) {
        verificarPermissaoAdmin();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        if (dto.nome() != null && !dto.nome().equals(categoria.getNome())) {
            if (categoriaRepository.existsByNome(dto.nome())) {
                throw new ConflictException("Já existe uma categoria com este nome");
            }
            categoria.setNome(dto.nome());
        }

        if (dto.slug() != null && !dto.slug().equals(categoria.getSlug())) {
            if (categoriaRepository.existsBySlug(dto.slug())) {
                throw new ConflictException("Já existe uma categoria com este slug");
            }
            categoria.setSlug(dto.slug());
        }

        return toResponseDto(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deleteCategoria(UUID id) {
        verificarPermissaoAdmin();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        categoriaRepository.delete(categoria);
    }

    public Page<CategoriaResponseDto> getAll(Integer pagina, Integer resultados, String busca) {
        Sort sort = Sort.by("nome").ascending();

        List<Categoria> categorias = categoriaRepository.findAll(sort);

        if (busca != null && !busca.isBlank()) {
            String buscaLower = busca.toLowerCase();
            categorias = categorias.stream()
                    .filter(c -> c.getNome().toLowerCase().contains(buscaLower))
                    .collect(Collectors.toList());
        }

        int total = categorias.size();

        int pageNum = (pagina != null && pagina >= 0) ? pagina : 0;
        int pageSize = (resultados != null && resultados > 0) ? resultados : 10;

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        int start = (int) pageable.getOffset();

        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        int end = Math.min(start + pageSize, total);
        List<CategoriaResponseDto> content = categorias.subList(start, end).stream()
                .map(this::toResponseDto)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    public CategoriaResponseDto getCategoriaById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return toResponseDto(categoria);
    }

    private void verificarPermissaoAdmin() {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        if (!"admin".equalsIgnoreCase(usuarioLogado.getPapel())) {
            throw new ForbiddenException("Apenas administradores podem gerenciar categorias");
        }
    }

    private CategoriaResponseDto toResponseDto(Categoria categoria) {
        return new CategoriaResponseDto(
                categoria.getId(),
                categoria.getNome(),
                categoria.getSlug()
        );
    }
}