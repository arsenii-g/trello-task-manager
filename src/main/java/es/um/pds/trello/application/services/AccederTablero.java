package es.um.pds.trello.application.services;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.TableroMapper;
import es.um.pds.trello.application.ports.in.IAccederTablero;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;

@Service
public class AccederTablero implements IAccederTablero {

    private final TableroRepository tableroRepo;

    public AccederTablero(TableroRepository tableroRepo) {
        this.tableroRepo = tableroRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public TableroDTO ejecutar(String urlValor) {
        URL url = new URL(urlValor);

        Tablero tablero = tableroRepo.findById(url)
                .orElseThrow(() -> new IllegalArgumentException("El tablero con URL " + urlValor + " no existe."));

        return TableroMapper.toDTO(tablero);
    }
}