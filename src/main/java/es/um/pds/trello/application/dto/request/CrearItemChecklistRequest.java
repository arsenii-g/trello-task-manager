package es.um.pds.trello.application.dto.request;

public record CrearItemChecklistRequest(
	    String urlTablero,
	    String idTarjeta,
	    String textoItem,
	    String emailAutor
) {}
