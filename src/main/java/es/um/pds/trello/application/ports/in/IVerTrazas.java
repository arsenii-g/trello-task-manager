package es.um.pds.trello.application.ports.in;

import java.util.List;

import es.um.pds.trello.application.dto.TrazaDTO;

public interface IVerTrazas {
	List<TrazaDTO> ejecutar(String urlTablero);
}
