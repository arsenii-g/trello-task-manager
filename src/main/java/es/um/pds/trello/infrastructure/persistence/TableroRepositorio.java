package es.um.pds.trello.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;
import java.util.Optional;

@Repository
public class TableroRepositorio implements TableroRepository {

    private final JpaTableroRepository jpa;

    public TableroRepositorio(JpaTableroRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Tablero tablero) {
        jpa.save(tablero);
    }

    @Override
    public Optional<Tablero> findById(URL url) {
        return jpa.findById(url);
    }
}