package es.um.pds.trello.application.dto;

import es.um.pds.trello.domain.model.ListaTareas;
import java.util.stream.Collectors;

public class ListaMapper {
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
}