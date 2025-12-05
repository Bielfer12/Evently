package com.backend.evently.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "comentarios")
@EntityListeners(AuditingEntityListener.class)
public class Comentario {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comentario_pai")
    private Comentario comentarioPai;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "avaliacao")
    private Short avaliacao;

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private Instant criadoEm;
}