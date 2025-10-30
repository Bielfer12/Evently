package com.backend.evently.model;

import com.backend.evently.enums.StatusEventoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "eventos")
@EntityListeners(AuditingEntityListener.class)
public class Evento {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true)
    private String slug;

    private String descricao_curta;

    private String descricao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEventoEnum statusEventoEnum;

    private Integer capacidade;

    private String metadados;

    @ManyToOne
    @JoinColumn(name = "id_organizador")
    private Organizador organizador;

    @ManyToOne
    @JoinColumn(name = "id_local")
    private Local local;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @CreatedBy
    @Column(name = "criado_por", updatable = false)
    private UUID criadoPor;

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private Instant criadoEm;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private Instant atualizadoEm;
}