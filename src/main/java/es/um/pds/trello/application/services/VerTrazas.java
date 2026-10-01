package es.um.pds.trello.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.TrazaDTO;
import es.um.pds.trello.application.dto.TrazaMapper;
import es.um.pds.trello.application.ports.in.IVerTrazas;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.domain.model.URL;

@Service
public class VerTrazas implements IVerTrazas{

    private final TrazaRepository trazaRepo;

    public VerTrazas(TrazaRepository trazaRepo) {
        this.trazaRepo = trazaRepo;
    }

    @Transactional(readOnly = true)
    public List<TrazaDTO> ejecutar(String urlTablero) {
        URL url = new URL(urlTablero);

        return trazaRepo.findByTableroId(url)
                .stream()
                .map(TrazaMapper::toDTO)
                .toList();
    }
}