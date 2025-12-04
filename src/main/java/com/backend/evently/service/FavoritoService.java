package com.backend.evently.service;

import com.backend.evently.config.security.CurrentUserService;
import com.backend.evently.dto.evento.EventoResponseDto;
import com.backend.evently.exception.ConflictException;
import com.backend.evently.exception.ResourceNotFoundException;
import com.backend.evently.model.Evento;
import com.backend.evently.model.Favorito;
import com.backend.evently.model.FavoritoId;
import com.backend.evently.model.Usuario;
import com.backend.evently.repository.EventoRepository;
import com.backend.evently.repository.FavoritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final EventoRepository eventoRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void adicionarFavorito(UUID idEvento) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        // Verifica se o evento existe
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        FavoritoId favoritoId = new FavoritoId(usuarioLogado.getId(), evento.getId());

        // Evita duplicidade
        if (favoritoRepository.existsById(favoritoId)) {
            throw new ConflictException("Este evento já está nos seus favoritos");
        }

        Favorito favorito = new Favorito(usuarioLogado, evento);
        favoritoRepository.save(favorito);
    }

    @Transactional
    public void removerFavorito(UUID idEvento) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        FavoritoId favoritoId = new FavoritoId(usuarioLogado.getId(), idEvento);

        Favorito favorito = favoritoRepository.findById(favoritoId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito não encontrado"));

        favoritoRepository.delete(favorito);
    }

    public boolean isFavorito(UUID idEvento) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();
        FavoritoId favoritoId = new FavoritoId(usuarioLogado.getId(), idEvento);
        return favoritoRepository.existsById(favoritoId);
    }

    @Transactional(readOnly = true)
    public Page<EventoResponseDto> listarFavoritosDoUsuario(Integer pagina, Integer resultados) {
        Usuario usuarioLogado = currentUserService.getCurrentUser();

        // Ajuste de paginação
        int pageNum = (pagina != null && pagina >= 0) ? pagina : 0;
        int pageSize = (resultados != null && resultados > 0) ? resultados : 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // Busca na tabela de favoritos e mapeia para o DTO de Evento
        return favoritoRepository.findAllByUsuarioId(usuarioLogado.getId(), pageable)
                .map(favorito -> toEventoResponseDto(favorito.getEvento()));
    }

    // Método auxiliar para converter Model -> DTO (Copiado/Adaptado do EventoService)
    // Em um projeto real, idealmente isso estaria em um Mapper separado para evitar duplicação
    private EventoResponseDto toEventoResponseDto(Evento evento) {
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