package es.um.pds.trello.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.TrazaId;
import es.um.pds.trello.domain.model.URL;
import java.util.List;

public interface JpaTrazaRepository extends JpaRepository<Traza, TrazaId> {

    List<Traza> findByTableroId(URL tableroId);
}