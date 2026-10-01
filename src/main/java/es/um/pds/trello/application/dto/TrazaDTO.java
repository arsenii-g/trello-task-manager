package es.um.pds.trello.application.dto;

import java.time.LocalDateTime;

public record TrazaDTO(
	    String emailAutor,
	    String tipoObjeto,
	    String accion,
	    String detalles,
	    LocalDateTime fecha,
	    String idObjetoAfectado
) {}