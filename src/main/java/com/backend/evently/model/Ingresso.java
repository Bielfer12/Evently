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
@Table(name = "ingressos")
@EntityListeners(AuditingEntityListener.class)
public class Ingresso {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String nome;

    // Relacionamento com Evento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    /* Campos comentados no SQL original:
    private BigDecimal preco;
    private Integer quantidade;
    private Instant vendasInicio;
    private Instant vendasFim;
    */

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private Instant criadoEm;
}