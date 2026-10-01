package es.um.pds.trello.application.dto;

import java.util.stream.Collectors;
import es.um.pds.trello.domain.model.*;

public class TableroMapper {

    public static TableroDTO toDTO(Tablero tablero) {
        if (tablero == null) return null;

        return new TableroDTO(
            tablero.getTitulo(),
            tablero.getUrl().valor(),
            tablero.getEstado().nombre(),
            tablero.getDueñoId().valor(),
            tablero.getListas().stream()
                .map(TableroMapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    public static ListaDTO toDTO(ListaTareas lista) {
        if (lista == null) return null;

        return new ListaDTO(
            lista.getId(),
            lista.getTitulo(),
            lista.getPosicion(),
            lista.isEsListaCompletadas(),
            lista.getTarjetas().stream()
                .map(TarjetaMapper::toDTO)
                .collect(Collectors.toList()),
            lista.getLimiteTarjetas(),
            lista.getPredecesorasObligatorias().stream()
                .map(ListaTareas::getId)
                .collect(Collectors.toList())
        );
    }
    public static TarjetaDTO toDTO(Tarjeta tarjeta) {
        if (tarjeta == null) return null;

        java.util.List<TarjetaDTO.ItemChecklistDTO> items = new java.util.ArrayList<>();
        if (tarjeta instanceof Checklist checklist) {
            items = checklist.getItems().stream()
                .map(i -> new TarjetaDTO.ItemChecklistDTO(i.descripcion(), i.estado()))
                .collect(Collectors.toList());
        }

        return new TarjetaDTO(
            tarjeta.getId().valor(),
            tarjeta.getTitulo(),
            tarjeta.getDescripcion(),
            tarjeta.isCompletada(),
            tarjeta.getClass().getSimpleName(),
            tarjeta.getEtiquetas().stream()
                .map(e -> e.nombre() + "|" + e.color())
                .collect(Collectors.toList()),
            items
        );
    }

    public static TrazaDTO toDTO(Traza traza) {
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