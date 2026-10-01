package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.AñadirEtiquetaRequest;

public interface IAñadirEtiqueta {
	void ejecutar(AñadirEtiquetaRequest request);
}
