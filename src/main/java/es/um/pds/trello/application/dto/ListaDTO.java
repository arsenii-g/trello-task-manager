package es.um.pds.trello.application.dto;

import java.util.List;

public record ListaDTO(
	    String id,
	    String titulo,
	    int posicion,
	    boolean esListaCompletadas,
	    List<TarjetaDTO> tarjetas,
	    Integer limiteTarjetas,
	    List<String> predecesorasIds
	) {}
