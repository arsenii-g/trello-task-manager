package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.AñadirTarjetaRequest;

public interface IAñadirTarjeta {
	void ejecutar(AñadirTarjetaRequest request);
}
