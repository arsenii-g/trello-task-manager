package es.um.pds.trello.domain.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Email(
    @Column(name = "email_valor", nullable = false)
    String valor
) implements Serializable {

    private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("R3: El email no puede estar vacío.");
        }
        if (!valor.matches(REGEX_EMAIL)) {
            throw new IllegalArgumentException("R3: El formato del email no es válido: " + valor);
        }
    }

    public String getValor() { return valor; }
}