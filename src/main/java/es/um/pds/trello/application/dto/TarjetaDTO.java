package es.um.pds.trello.application.dto;

import java.util.List;

public record TarjetaDTO(
	    String id,
	    String titulo,
	    String descripcion,
	    boolean completada,
	    String tipo,
	    List<String> etiquetas,
	    List<ItemChecklistDTO> items
	) {
	    public record ItemChecklistDTO(String contenido, boolean completado) {}
	}
