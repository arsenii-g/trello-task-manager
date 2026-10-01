package es.um.pds.trello.domain.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Estado(
    @Column(name = "estado_nombre", nullable = false)
    String nombre
) implements Serializable {

    public static final String BLOQUEADO = "BLOQUEADO";
    public static final String ACTIVO = "ACTIVO";

    public Estado {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El estado no puede estar vacío.");
        }

        String nombreUpper = nombre.toUpperCase();
        if (!List.of(BLOQUEADO, ACTIVO).contains(nombreUpper)) {
            throw new IllegalArgumentException("Estado no reconocido: " + nombre);
        }
    }

    public static Estado activo() {
        return new Estado(ACTIVO);
    }

    public static Estado bloqueado() {
        return new Estado(BLOQUEADO);
    }

    public boolean esBloqueado() {
        return BLOQUEADO.equals(this.nombre.toUpperCase());
    }
}