package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.CambiarEstadoTarjetaRequest;

public interface ICambiarEstadoTarjeta {
	void ejecutar(CambiarEstadoTarjetaRequest request);
}
