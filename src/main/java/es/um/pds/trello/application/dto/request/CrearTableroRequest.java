package es.um.pds.trello.application.dto.request;

public record CrearTableroRequest(
	    String emailDueño,
	    String nombreDueño,
	    String tituloTablero
) {}
