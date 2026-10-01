package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.domain.services.GeneradorTrazas;

import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;
import es.um.pds.trello.domain.model.Tarea;
import es.um.pds.trello.domain.model.Tarjeta;
import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.application.dto.request.AñadirTarjetaRequest;
import es.um.pds.trello.application.ports.in.IAñadirTarjeta;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.Checklist;
import es.um.pds.trello.domain.model.Email;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.TipoAccion;

@Service
public class AñadirTarjeta implements IAñadirTarjeta{

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;

    public AñadirTarjeta(TableroRepository tableroRepo,
                         UsuarioRepository usuarioRepo,
                         TrazaRepository trazaRepo,
                         GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
    }

    @Transactional
    public void ejecutar(AñadirTarjetaRequest request) {
        URL url = new URL(request.urlTablero());
        Email email = new Email(request.emailAutor());

        Tablero tablero = tableroRepo.findById(url)
                .orElseThrow(() -> new IllegalArgumentException("Tablero no encontrado"));

        Usuario autor = usuarioRepo.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado (R3)"));

        Tarjeta nuevaTarjeta;
        if ("CHECKLIST".equalsIgnoreCase(request.tipoTarjeta())) {
            nuevaTarjeta = new Checklist(request.titulo(), request.descripcion());
        } else {
            nuevaTarjeta = new Tarea(request.titulo(), request.descripcion());
        }

        tablero.añadirTarjetaALista(request.idLista(), nuevaTarjeta);

        Traza traza = generadorTrazas.crearTrazaTarjeta(
            autor,
            tablero,
            nuevaTarjeta.getId(),
            TipoAccion.CREAR
        );

        tableroRepo.save(tablero);
        trazaRepo.save(traza);
    }
}