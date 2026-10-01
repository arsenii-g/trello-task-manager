package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.EliminarTarjetaRequest;

public interface IEliminarTarjeta {
	void ejecutar(EliminarTarjetaRequest request);
}
