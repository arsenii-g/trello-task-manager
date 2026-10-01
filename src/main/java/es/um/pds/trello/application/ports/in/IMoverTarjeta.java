package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.MoverTarjetasRequest;

public interface IMoverTarjeta {
	void ejecutar(MoverTarjetasRequest request);
}
