package es.um.pds.trello.application.dto.request;

import java.util.List;

public record ConfigurarWorkflowRequest(
	    String urlTablero,
	    String idListaDestino,
	    List<String> nuevosIdsPredecesores,
	    String emailAutor
	) {}
