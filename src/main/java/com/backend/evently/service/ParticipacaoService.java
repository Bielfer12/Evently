package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.participacao.ParticipacaoCreateDto;
import com.backend.evently.dto.participacao.ParticipacaoResponseDto;
import com.backend.evently.enums.StatusEventoEnum;
import com.backend.evently.exception.ConflictException;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Ingresso;
import com.backend.evently.model.Organizador;
import com.backend.evently.model.Participacao;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.EventoRepository;
import com.backend.evently.repository.IngressoRepository;
import com.backend.evently.repository.OrganizadorRepository;
import com.backend.evently.repository.ParticipacaoRepository;
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
public class ParticipacaoService {

    private final ParticipacaoRepository participacaoRepository;
    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;
    private final OrganizadorRepository organizadorRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ParticipacaoResponseDto registrarParticipacao(ParticipacaoCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        Ingresso ingresso = ingressoRepository.findById(dto.idIngresso())
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        Evento evento = ingresso.getEvento();

        if (evento.getStatusEventoEnum() != StatusEventoEnum.PUBLICADO) {
            throw new ConflictException("Não é possível participar de um evento cancelado");
        }

        boolean jaParticipa = participacaoRepository.existsByUsuarioIdAndEventoId(
                usuarioLogado.getId(),
                evento.getId()
        );

        if (jaParticipa) {
            throw new ConflictException("Você já está participando deste evento");
        }

        Participacao participacao = new Participacao();
        participacao.setUsuario(usuarioLogado);
        participacao.setIngresso(ingresso);
        participacao.setEvento(evento);

        Participacao salvo = participacaoRepository.save(participacao);
        return toResponseDto(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ParticipacaoResponseDto> listarMinhasParticipacoes(Integer pagina, Integer resultados) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        Pageable pageable = PageRequest.of(pagina, resultados, Sort.by("criadoEm").descending());

        return participacaoRepository.findAllByUsuarioId(usuarioLogado.getId(), pageable)
                .map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ParticipacaoResponseDto> listarParticipantesPorEvento(UUID idEvento, Integer pagina, Integer resultados) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        if (!isAdmin) {
            Organizador organizadorLogado = organizadorRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ForbiddenException("Você não tem perfil de organizador."));

            UUID idOrganizadorDoEvento = evento.getOrganizador().getId();

            if (!idOrganizadorDoEvento.equals(organizadorLogado.getId())) {
                throw new ForbiddenException("Você não tem permissão para ver os participantes deste evento.");
            }
        }

        Pageable pageable = PageRequest.of(pagina, resultados, Sort.by("criadoEm").descending());
        return participacaoRepository.findAllByEventoId(idEvento, pageable)
                .map(this::toResponseDto);
    }

    @Transactional
    public void cancelarParticipacao(UUID idParticipacao) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        Participacao participacao = participacaoRepository.findById(idParticipacao)
                .orElseThrow(() -> new ResourceNotFoundException("Participação não encontrada"));

        if (!isAdmin && !participacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("Você não tem permissão para cancelar esta participação");
        }

        participacaoRepository.delete(participacao);
    }

    private ParticipacaoResponseDto toResponseDto(Participacao p) {
        return new ParticipacaoResponseDto(
                p.getId(),
                p.getEvento().getId(),
                p.getEvento().getTitulo(),
                p.getIngresso() != null ? p.getIngresso().getId() : null,
                p.getIngresso() != null ? p.getIngresso().getNome() : "Ingresso Removido",
                p.getUsuario().getNome(),
                p.getCriadoEm()
        );
    }
}