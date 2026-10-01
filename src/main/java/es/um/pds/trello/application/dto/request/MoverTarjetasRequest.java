package es.um.pds.trello.application.dto.request;

public record MoverTarjetasRequest(
	    String urlTablero,
	    String idTarjeta,
	    String idListaOrigen,
	    String idListaDestino,
	    String emailAutor
	) {}