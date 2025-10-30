package com.backend.evently.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "organizadores")
@EntityListeners(AuditingEntityListener.class)
public class Organizador {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "email_contato")
    private String emailContato;

    @Column(name = "telefone_contato")
    private String telefoneContato;

    private String site;

    @OneToMany(mappedBy = "organizador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos = new ArrayList<>();

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private Instant criadoEm;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private Instant atualizadoEm;
}