package es.um.pds.trello.application.dto.request;

public record EliminarListaTareasRequest(
	    String urlTablero,
	    String idLista,
	    String emailAutor
) {}