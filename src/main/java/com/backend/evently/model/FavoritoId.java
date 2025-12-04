package com.backend.evently.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FavoritoId implements Serializable {

    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Column(name = "id_evento")
    private UUID idEvento;
}