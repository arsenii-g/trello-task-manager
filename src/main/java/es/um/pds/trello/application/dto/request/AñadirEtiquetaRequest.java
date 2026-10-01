package es.um.pds.trello.application.dto.request;

import es.um.pds.trello.application.dto.EtiquetaDTO;

public record AñadirEtiquetaRequest(
	    String urlTablero,
	    String idTarjeta,
	    String emailAutor,
	    EtiquetaDTO etiqueta
	) {}