package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.TableroDTO;

public interface IAccederTablero {
	TableroDTO ejecutar(String urlValor);
}
