package es.um.pds.trello.application.dto.request;

public record ConfigurarLimiteListaRequest(
	    String urlTablero,
	    String idLista,
	    String emailAutor,
	    Integer nuevoLimite
	) {}