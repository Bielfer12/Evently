package com.backend.evently.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Data // Gera Getters, Setters, Equals e HashCode (obrigatório para chaves compostas)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // Diz pro Java: "Isso pode ser embutido dentro de outra classe como ID"
public class FavoritoId implements Serializable {
    private UUID idUsuario;
    private UUID idEvento;
}