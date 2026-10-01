package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.BloquearTableroRequest;

public interface IBloquearTablero {
	void ejecutar(BloquearTableroRequest request);
}
