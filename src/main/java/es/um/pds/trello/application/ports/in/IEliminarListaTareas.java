package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.EliminarListaTareasRequest;

public interface IEliminarListaTareas {
	void ejecutar(EliminarListaTareasRequest request);
}
