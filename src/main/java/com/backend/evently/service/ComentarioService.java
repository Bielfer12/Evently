package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.comentario.ComentarioCreateDto;
import com.backend.evently.dto.comentario.ComentarioResponseDto;
import com.backend.evently.dto.comentario.ComentarioUpdateDto;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Comentario;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.ComentarioRepository;
import com.backend.evently.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final EventoRepository eventoRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ComentarioResponseDto criar(ComentarioCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        Evento evento = eventoRepository.findById(dto.idEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        Comentario comentarioPai = null;
        if (dto.idComentarioPai() != null) {
            comentarioPai = comentarioRepository.findById(dto.idComentarioPai())
                    .orElseThrow(() -> new ResourceNotFoundException("Comentário pai não encontrado"));
        }

        Comentario comentario = new Comentario();
        comentario.setEvento(evento);
        comentario.setUsuario(usuarioLogado);
        comentario.setComentarioPai(comentarioPai);
        comentario.setConteudo(dto.conteudo());
        comentario.setAvaliacao(dto.avaliacao());

        Comentario salvo = comentarioRepository.save(comentario);
        return toResponseDto(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ComentarioResponseDto> listarPorEvento(UUID idEvento, Integer pagina, Integer resultados, boolean apenasRaiz) {
        eventoRepository.findById(idEvento)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        Pageable pageable = PageRequest.of(pagina, resultados, Sort.by("criadoEm").descending());

        Page<Comentario> comentarios;
        if (apenasRaiz) {
            comentarios = comentarioRepository.findAllByEventoIdAndComentarioPaiIsNull(idEvento, pageable);
        } else {
            comentarios = comentarioRepository.findAllByEventoId(idEvento, pageable);
        }

        return comentarios.map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ComentarioResponseDto> listarRespostas(UUID idComentarioPai, Integer pagina, Integer resultados) {
        comentarioRepository.findById(idComentarioPai)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        Pageable pageable = PageRequest.of(pagina, resultados, Sort.by("criadoEm").ascending());

        return comentarioRepository.findAllByComentarioPaiId(idComentarioPai, pageable)
                .map(this::toResponseDto);
    }

    @Transactional
    public ComentarioResponseDto atualizar(UUID id, ComentarioUpdateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        if (!isAdmin && !comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("Você não tem permissão para editar este comentário");
        }

        if (dto.conteudo() != null) {
            comentario.setConteudo(dto.conteudo());
        }
        if (dto.avaliacao() != null) {
            comentario.setAvaliacao(dto.avaliacao());
        }

        Comentario atualizado = comentarioRepository.save(comentario);
        return toResponseDto(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        if (!isAdmin && !comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar este comentário");
        }

        comentarioRepository.deleteAllByComentarioPaiId(comentario.getId());

        comentarioRepository.delete(comentario);
    }

    private ComentarioResponseDto toResponseDto(Comentario c) {
        return new ComentarioResponseDto(
                c.getId(),
                c.getEvento().getId(),
                c.getEvento().getTitulo(),
                c.getUsuario() != null ? c.getUsuario().getId() : null,
                c.getUsuario() != null ? c.getUsuario().getNome() : "Usuário Removido",
                c.getComentarioPai() != null ? c.getComentarioPai().getId() : null,
                c.getConteudo(),
                c.getAvaliacao(),
                c.getCriadoEm()
        );
    }
}