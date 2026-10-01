package es.um.pds.trello.application.ports.out;

import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;
import java.util.Optional;

public interface TableroRepository {
    void save(Tablero tablero);
    Optional<Tablero> findById(URL url);
}