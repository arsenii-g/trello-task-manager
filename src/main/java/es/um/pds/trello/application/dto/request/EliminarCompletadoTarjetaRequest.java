package es.um.pds.trello.application.dto.request;

public record EliminarCompletadoTarjetaRequest(
	    String urlTablero,
	    String idTarjeta,
	    String emailAutor
) {}