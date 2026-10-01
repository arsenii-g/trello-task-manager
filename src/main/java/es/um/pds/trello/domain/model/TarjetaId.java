package es.um.pds.trello.domain.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record TarjetaId(
    @Column(name = "tarjeta_id_valor", nullable = false)
    String valor
) implements Serializable {

    public TarjetaId {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El ID de la tarjeta no puede estar vacío.");
        }
    }

    public static TarjetaId generar() {
        return new TarjetaId(UUID.randomUUID().toString());
    }
}
