package es.um.pds.trello.application.dto.request;

public record AñadirTarjetaRequest(
	    String urlTablero,
	    String idLista,
	    String titulo,
	    String descripcion,
	    String emailAutor,
	    String tipoTarjeta
	) {}