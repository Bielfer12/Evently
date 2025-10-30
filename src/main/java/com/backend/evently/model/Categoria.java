package com.backend.evently.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(unique = true)
    private String nome;

    @Column(unique = true)
    private String slug;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos = new ArrayList<>();
}
