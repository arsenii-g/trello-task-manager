package es.um.pds.trello.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;

public interface JpaTableroRepository extends JpaRepository<Tablero, URL> {

}