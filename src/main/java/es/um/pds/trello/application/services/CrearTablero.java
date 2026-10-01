package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.TableroMapper;
import es.um.pds.trello.application.dto.request.CrearTableroRequest;
import es.um.pds.trello.application.ports.in.ICrearTablero;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import es.um.pds.trello.domain.model.Email;
import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.TipoAccion;

@Service
public class CrearTablero implements ICrearTablero{

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;

    public CrearTablero(TableroRepository tableroRepo,
                        UsuarioRepository usuarioRepo,
                        TrazaRepository trazaRepo,
                        GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
    }

    @Transactional
    public TableroDTO ejecutar(CrearTableroRequest request) {

        Email email = new Email(request.emailDueño());

        Usuario dueño = usuarioRepo.findById(email)
                .orElseGet(() -> usuarioRepo.save(new Usuario(email, request.nombreDueño())));

        Tablero nuevoTablero = new Tablero(request.tituloTablero(), dueño.getId());

        Traza trazaCreacion = generadorTrazas.crearTrazaTablero(
                dueño,
                nuevoTablero,
                TipoAccion.CREAR
        );

        tableroRepo.save(nuevoTablero);
        trazaRepo.save(trazaCreacion);

        return TableroMapper.toDTO(nuevoTablero);
    }
}