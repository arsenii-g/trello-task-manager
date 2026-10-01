package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.EliminarCompletadoTarjetaRequest;

public interface IEliminarCompletadoTarjeta {
	void ejecutar(EliminarCompletadoTarjetaRequest request);
}
