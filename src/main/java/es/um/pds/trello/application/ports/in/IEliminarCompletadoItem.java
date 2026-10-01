package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.EliminarCompletadoItemRequest;

public interface IEliminarCompletadoItem {
	void ejecutar(EliminarCompletadoItemRequest request);
}
