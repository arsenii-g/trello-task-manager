package es.um.pds.trello.application.dto.request;

public record CambiarEstadoTarjetaRequest(
	    String urlTablero,
	    String idTarjeta,
	    boolean completada,
	    String emailAutor
	) {}