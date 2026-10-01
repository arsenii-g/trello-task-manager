package es.um.pds.trello.domain.model;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private Email id;

    private String nombre;

    protected Usuario() {}

    public Usuario(Email id, String nombre) {
        if (id == null) {
            throw new IllegalArgumentException("R3: El identificador (Email) es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        this.id = id;
        this.nombre = nombre;
    }

    public Usuario(String email, String nombre) {
        this(new Email(email), nombre);
    }

    public Email getId() {
        return id;
    }

    public String getEmail() {
        return id.valor();
    }

    public String getNombre() {
        return nombre;
    }

    public void cambiarNombre(String nuevoNombre) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            this.nombre = nuevoNombre;
        } else {
            throw new IllegalArgumentException("El nuevo nombre no es válido.");
        }
    }
}