package es.um.pds.trello.application.dto;

import es.um.pds.trello.domain.model.Traza;

public class TrazaMapper {
    public static TrazaDTO toDTO(Traza traza) {
        if (traza == null) return null;
        return new TrazaDTO(
            traza.getAutor().valor(),
            traza.getTipoObjeto().name(),
            traza.getAccion().name(),
            traza.getDetalles(),
            traza.getFecha(),
            traza.getObjetoAfectadoId() != null ? traza.getObjetoAfectadoId().valor() : null
        );
    }
}