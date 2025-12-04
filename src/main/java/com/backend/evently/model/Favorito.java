package com.backend.evently.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "favoritos")
public class Favorito {

    @EmbeddedId
    private FavoritoId id;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("idEvento")
    @JoinColumn(name = "id_evento")
    private Evento evento;

    public Favorito() {
        this.id = new FavoritoId();
    }

    public Favorito(Usuario usuario, Evento evento) {
        this.id = new FavoritoId(usuario.getId(), evento.getId());
        this.usuario = usuario;
        this.evento = evento;
    }
}