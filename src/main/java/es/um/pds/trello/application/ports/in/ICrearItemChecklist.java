package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.CrearItemChecklistRequest;

public interface ICrearItemChecklist {
	void ejecutar(CrearItemChecklistRequest request);
}
