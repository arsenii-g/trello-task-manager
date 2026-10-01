package es.um.pds.trello.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import java.util.regex.Pattern;
import jakarta.persistence.*;

@Embeddable
public record URL(
    @Column(name = "url_valor", nullable = false, unique = true)
    String valor
) implements Serializable {

    private static final String REGEX_URL = "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}/.*$";

    public URL {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("La URL no puede estar vacía.");
        }
        if (!Pattern.matches(REGEX_URL, valor)) {
            throw new IllegalArgumentException("El formato de la URL no es válido: " + valor);
        }
    }

    public static URL generar() {
        String nuevaUrl = "https://trello.com/tableros/" + UUID.randomUUID().toString();
        return new URL(nuevaUrl);
    }

    @Override
    public String toString() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URL url)) return false;
        return Objects.equals(valor, url.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}