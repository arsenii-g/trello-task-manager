package es.um.pds.trello.application.ports.in;

import es.um.pds.trello.application.dto.TableroDTO;

public interface ICrearDesdePlantilla {
    TableroDTO ejecutar(String emailDueño, String nombreDueño, String yamlContent);
}
