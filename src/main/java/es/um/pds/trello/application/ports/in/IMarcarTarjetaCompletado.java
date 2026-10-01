package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.MarcarTarjetaCompletadoRequest;

public interface IMarcarTarjetaCompletado {
	void ejecutar(MarcarTarjetaCompletadoRequest request);
}
