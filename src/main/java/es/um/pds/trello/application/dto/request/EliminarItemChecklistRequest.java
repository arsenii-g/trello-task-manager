package es.um.pds.trello.application.dto.request;

public record EliminarItemChecklistRequest(
	    String urlTablero,
	    String idTarjeta,
	    int indiceItem,
	    String emailAutor
) {}