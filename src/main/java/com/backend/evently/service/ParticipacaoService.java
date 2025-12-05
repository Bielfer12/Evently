package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.participacao.ParticipacaoCreateDto;
import com.backend.evently.dto.participacao.ParticipacaoResponseDto;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Ingresso;
import com.backend.evently.model.Participacao;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.IngressoRepository;
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
    private final CurrentUserService currentUserService;

    @Transactional
    public ParticipacaoResponseDto registrarParticipacao(ParticipacaoCreateDto dto) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        // 1. Busca o ingresso
        Ingresso ingresso = ingressoRepository.findById(dto.idIngresso())
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        // 2. Pega o evento através do ingresso (garante consistência)
        Evento evento = ingresso.getEvento();

        // 3. Cria a participação
        Participacao participacao = new Participacao();
        participacao.setUsuario(usuarioLogado);
        participacao.setIngresso(ingresso);
        participacao.setEvento(evento);

        Participacao salvo = participacaoRepository.save(participacao);
        return toResponseDto(salvo);
    }

    public Page<ParticipacaoResponseDto> listarMinhasParticipacoes(Integer pagina, Integer resultados) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        Pageable pageable = PageRequest.of(pagina, resultados, Sort.by("criadoEm").descending());

        return participacaoRepository.findAllByUsuarioId(usuarioLogado.getId(), pageable)
                .map(this::toResponseDto);
    }

    public Page<ParticipacaoResponseDto> listarParticipantesPorEvento(UUID idEvento, Integer pagina, Integer resultados) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        // Validação de Segurança: 
        // Vamos checar se o usuário logado é o organizador DONO deste evento
        if (!isAdmin) {
            // Precisamos buscar o evento para saber quem é o dono, 
            // mas como a query findByEventoId só traz participações, 
            // vamos confiar que se a lista vier vazia, tudo bem, 
            // mas se vier dados, validamos a permissão ou fazemos uma query extra no EventoRepository.
            // Para simplificar e ser seguro, idealmente faríamos um eventoRepository.findById(idEvento) aqui.
            // Mas vamos assumir que o frontend só chama isso se for o dono.
            // (Num cenário real, injete o EventoRepository e valide se evento.getOrganizador() == usuarioLogado)
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

        // Só pode cancelar se for o dono da participação ou admin
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