package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.ingresso.IngressoCreateDto;
import com.backend.evently.dto.ingresso.IngressoResponseDto;
import com.backend.evently.dto.ingresso.IngressoUpdateDto;
import com.backend.evently.exception.ForbiddenException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Ingresso;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.EventoRepository;
import com.backend.evently.repository.IngressoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public IngressoResponseDto createIngresso(IngressoCreateDto dto) {
        // 1. Busca o evento
        Evento evento = eventoRepository.findById(dto.idEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        // 2. Valida permissão (Só o dono do evento ou admin pode criar ingressos)
        validarPermissaoDonoDoEvento(evento);

        // 3. Cria o ingresso
        Ingresso ingresso = new Ingresso();
        ingresso.setNome(dto.nome());
        ingresso.setEvento(evento);

        Ingresso salvo = ingressoRepository.save(ingresso);
        return toResponseDto(salvo);
    }

    @Transactional
    public IngressoResponseDto updateIngresso(UUID id, IngressoUpdateDto dto) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        // Valida permissão no evento pai
        validarPermissaoDonoDoEvento(ingresso.getEvento());

        ingresso.setNome(dto.nome());

        Ingresso salvo = ingressoRepository.save(ingresso);
        return toResponseDto(salvo);
    }

    @Transactional
    public void deleteIngresso(UUID id) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        validarPermissaoDonoDoEvento(ingresso.getEvento());

        ingressoRepository.delete(ingresso);
    }

    // Listagem pública (qualquer um pode ver os tipos de ingressos disponíveis)
    public List<IngressoResponseDto> listByEvento(UUID idEvento) {
        return ingressoRepository.findByEventoId(idEvento).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public IngressoResponseDto getById(UUID id) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));
        return toResponseDto(ingresso);
    }

    // --- Métodos Auxiliares ---

    private void validarPermissaoDonoDoEvento(Evento evento) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getPapel());

        if (isAdmin) return; // Admin pode tudo

        // Se não é admin, precisa ser organizador
        if (usuarioLogado.getOrganizador() == null) {
            throw new ForbiddenException("Você não tem perfil de organizador.");
        }

        // E precisa ser o organizador DESTE evento específico
        UUID idOrganizadorDoEvento = evento.getOrganizador().getId();
        UUID idOrganizadorLogado = usuarioLogado.getOrganizador().getId();

        if (!idOrganizadorDoEvento.equals(idOrganizadorLogado)) {
            throw new ForbiddenException("Você não tem permissão para gerenciar ingressos deste evento.");
        }
    }

    private IngressoResponseDto toResponseDto(Ingresso ingresso) {
        return new IngressoResponseDto(
                ingresso.getId(),
                ingresso.getNome(),
                ingresso.getEvento().getId()
        );
    }
}