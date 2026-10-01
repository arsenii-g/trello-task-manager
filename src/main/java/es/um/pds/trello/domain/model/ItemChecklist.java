package es.um.pds.trello.domain.model;

import jakarta.persistence.Embeddable;

import jakarta.persistence.Column;

@Embeddable
public record ItemChecklist(
	    @Column(name = "contenido", nullable = false)
	    String descripcion,

	    @Column(name = "completado", nullable = false)
	    boolean estado
	) {
	    public ItemChecklist {
	        if (descripcion == null || descripcion.isBlank()) {
	            throw new IllegalArgumentException("La descripción del ítem no puede estar vacía");
	        }
	    }

	    public ItemChecklist(String descripcion) {
	        this(descripcion, false);
	    }

	}