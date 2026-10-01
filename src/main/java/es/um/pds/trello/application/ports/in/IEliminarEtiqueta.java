package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.EliminarEtiquetaRequest;

public interface IEliminarEtiqueta {
	void ejecutar(EliminarEtiquetaRequest request);
}
