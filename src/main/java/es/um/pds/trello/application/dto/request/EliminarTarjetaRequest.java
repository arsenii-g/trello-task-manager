package es.um.pds.trello.application.dto.request;

public record EliminarTarjetaRequest(
	    String urlTablero,
	    String idTarjeta,
	    String emailAutor
) {}