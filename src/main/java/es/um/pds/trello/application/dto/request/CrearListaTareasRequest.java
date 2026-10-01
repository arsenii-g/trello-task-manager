package es.um.pds.trello.application.dto.request;

public record CrearListaTareasRequest(
	    String urlTablero,
	    String titulo,
	    String email,
	    boolean esCompletada,
	    Integer limiteTarjetas
	) {
	}