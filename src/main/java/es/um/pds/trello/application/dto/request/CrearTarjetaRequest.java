package es.um.pds.trello.application.dto.request;

public record CrearTarjetaRequest(
	    String urlTablero,
	    String idLista,
	    String idTarjeta,
	    String titulo,
	    String descripcion,
	    String emailAutor,
	    String tipoTarjeta
) {}