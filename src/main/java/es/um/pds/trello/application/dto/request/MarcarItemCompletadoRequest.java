package es.um.pds.trello.application.dto.request;

public record MarcarItemCompletadoRequest(
	    String urlTablero,
	    String idTarjeta,
	    int indiceItem,
	    String emailAutor
) {}
