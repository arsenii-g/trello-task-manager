package es.um.pds.trello.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.TrazaId;
import es.um.pds.trello.domain.model.URL;
import java.util.List;
import java.util.Optional;

@Repository
public class TrazaRepositorio implements TrazaRepository {

    private final JpaTrazaRepository jpa;

    public TrazaRepositorio(JpaTrazaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Traza save(Traza traza) {
        return jpa.save(traza);
    }

    @Override
    public Optional<Traza> findById(TrazaId id) {
        return jpa.findById(id);
    }

    @Override
    public List<Traza> findByTableroId(URL urlTablero) {
        return jpa.findByTableroId(urlTablero);
    }
}