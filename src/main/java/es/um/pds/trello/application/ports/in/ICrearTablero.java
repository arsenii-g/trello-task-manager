package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.request.CrearTableroRequest;

public interface ICrearTablero {
	TableroDTO ejecutar(CrearTableroRequest request);
}
