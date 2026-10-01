package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.request.MarcarItemCompletadoRequest;

public interface IMarcarItemCompletado {
	void ejecutar(MarcarItemCompletadoRequest request);
}
