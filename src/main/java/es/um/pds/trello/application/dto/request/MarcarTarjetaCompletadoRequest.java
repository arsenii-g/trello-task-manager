package es.um.pds.trello.application.dto.request;

public record MarcarTarjetaCompletadoRequest(
	    String urlTablero,
	    String idTarjeta,
	    String emailAutor
) {}
