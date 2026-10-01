package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.CrearTarjetaRequest;

public interface ICrearTarjeta {
	void ejecutar(CrearTarjetaRequest request);
}
