package es.um.pds.trello.application.dto.request;

public record EliminarEtiquetaRequest(
	    String urlTablero,
	    String idTarjeta,
	    String emailAutor,
	    String nombreEtiqueta
) {}
