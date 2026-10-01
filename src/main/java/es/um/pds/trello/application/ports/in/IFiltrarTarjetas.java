package es.um.pds.trello.application.ports.in;

import java.util.List;
import es.um.pds.trello.application.dto.TableroDTO;

public interface IFiltrarTarjetas {
    TableroDTO ejecutar(String urlTablero, List<String> nombresEtiquetas);
}
