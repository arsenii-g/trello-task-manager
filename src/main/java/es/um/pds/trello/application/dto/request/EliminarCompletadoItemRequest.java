package es.um.pds.trello.application.dto.request;

public record EliminarCompletadoItemRequest(
	    String urlTablero,
	    String idTarjeta,
	    int indiceItem,
	    String emailAutor
) {}
