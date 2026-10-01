package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.EliminarItemChecklistRequest;

public interface IEliminarItemChecklist {
	void ejecutar(EliminarItemChecklistRequest request);
}
