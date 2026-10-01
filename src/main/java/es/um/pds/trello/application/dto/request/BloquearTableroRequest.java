package es.um.pds.trello.application.dto.request;

public record BloquearTableroRequest(
	    String urlTablero,
	    String emailAutor
) {}