package es.um.pds.trello.application.dto.template;

import java.util.List;

public record TemplateDTO(
    String titulo,
    List<TemplateListaDTO> listas
) {
    public record TemplateListaDTO(
        String titulo,
        boolean esListaCompletadas,
        Integer limite,
        List<TemplateTarjetaDTO> tarjetas
    ) {}

    public record TemplateTarjetaDTO(
        String titulo,
        String descripcion,
        String tipo,
        List<String> etiquetas
    ) {}
}
