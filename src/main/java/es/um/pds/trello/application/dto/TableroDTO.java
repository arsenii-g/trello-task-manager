package es.um.pds.trello.application.dto;

import java.util.List;

public record TableroDTO(
	    String titulo,
	    String url,
	    String estado,
	    String dueñoEmail,
	    List<ListaDTO> listas
) {}