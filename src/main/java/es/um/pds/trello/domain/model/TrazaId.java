package es.um.pds.trello.domain.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record TrazaId(
    @Column(name = "traza_id_valor", nullable = false)
    String valor
) implements Serializable {

    public TrazaId {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El ID de la traza no puede estar vacío.");
        }
    }

    public static TrazaId generar() {
        return new TrazaId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return valor;
    }
}
