package es.um.pds.trello.application.dto;

import es.um.pds.trello.domain.model.Checklist;
import es.um.pds.trello.domain.model.Tarjeta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TarjetaMapper {
    public static TarjetaDTO toDTO(Tarjeta tarjeta) {
        if (tarjeta == null) return null;

        List<TarjetaDTO.ItemChecklistDTO> items = new ArrayList<>();
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
}