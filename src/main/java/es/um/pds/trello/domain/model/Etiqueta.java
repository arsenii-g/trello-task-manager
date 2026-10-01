package es.um.pds.trello.domain.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import jakarta.persistence.*;

@Embeddable
public record Etiqueta(
    @Column(name = "etiqueta_nombre", nullable = false)
    String nombre,

    @Column(name = "etiqueta_color", nullable = false)
    String color
) implements Serializable {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final String COLOR_DEFECTO = "#808080";

    public Etiqueta {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la etiqueta no puede estar vacío.");
        }

        if (color == null || !HEX_PATTERN.matcher(color).matches()) {
            throw new IllegalArgumentException("El color debe tener un formato hexadecimal válido (ej: #FF5733)");
        }
    }

    public Etiqueta(String nombre) {
        this(nombre, COLOR_DEFECTO);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", nombre, color);
    }
}