package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.request.ConfigurarWorkflowRequest;
import es.um.pds.trello.application.ports.in.IConfigurarWorkflow;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;

@Service
public class ConfigurarWorkflow implements IConfigurarWorkflow {

    private final TableroRepository tableroRepo;

    public ConfigurarWorkflow(TableroRepository tableroRepo) {
        this.tableroRepo = tableroRepo;
    }

    @Override
    @Transactional
    public void ejecutar(ConfigurarWorkflowRequest request) {
        Tablero tablero = tableroRepo.findById(new URL(request.urlTablero()))
                .orElseThrow(() -> new IllegalArgumentException("Tablero no encontrado"));

        boolean existeLista = tablero.getListas().stream()
                .anyMatch(l -> l.getId().equals(request.idListaDestino()));

        if (!existeLista) {
            throw new IllegalArgumentException("La lista destino no existe en este tablero");
        }

        tablero.actualizarWorkflowDeLista(request.idListaDestino(), request.nuevosIdsPredecesores());

        tableroRepo.save(tablero);
    }
}
