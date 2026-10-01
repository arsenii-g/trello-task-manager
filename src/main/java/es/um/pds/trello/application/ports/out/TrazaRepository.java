package es.um.pds.trello.application.ports.out;

import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.TrazaId;
import es.um.pds.trello.domain.model.URL;

import java.util.List;
import java.util.Optional;

public interface TrazaRepository {

    Traza save(Traza traza);

    Optional<Traza> findById(TrazaId id);

    List<Traza> findByTableroId(URL urlTablero);
}