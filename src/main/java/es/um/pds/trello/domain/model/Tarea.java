package es.um.pds.trello.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tareas_simples")
@DiscriminatorValue("TAREA")
public class Tarea extends Tarjeta {

    protected Tarea() {
        super();
    }

    public Tarea(String titulo, String descripcion) {
        super(titulo, descripcion);
    }

    @Override
    public boolean puedeCompletarse() {
        return true;
    }

}