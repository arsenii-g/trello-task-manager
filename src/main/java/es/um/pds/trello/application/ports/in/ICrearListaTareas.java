package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.CrearListaTareasRequest;

public interface ICrearListaTareas {
	void ejecutar(CrearListaTareasRequest request);
}
