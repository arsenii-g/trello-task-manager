package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.request.EliminarEtiquetaRequest;
import es.um.pds.trello.application.ports.in.IEliminarEtiqueta;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.Email;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.TarjetaId;
import es.um.pds.trello.domain.model.TipoAccion;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.URL;
import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.domain.services.GeneradorTrazas;

@Service
public class EliminarEtiqueta implements IEliminarEtiqueta{

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;

    public EliminarEtiqueta(TableroRepository tableroRepo,
                            UsuarioRepository usuarioRepo,
                            TrazaRepository trazaRepo,
                            GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
    }

    @Transactional
    public void ejecutar(EliminarEtiquetaRequest request) {

        URL urlTablero = new URL(request.urlTablero());
        Email emailAutor = new Email(request.emailAutor());
        TarjetaId tarjetaId = new TarjetaId(request.idTarjeta());

        Tablero tablero = tableroRepo.findById(urlTablero)
                .orElseThrow(() -> new IllegalArgumentException("Tablero no encontrado."));

        Usuario autor = usuarioRepo.findById(emailAutor)
                .orElseThrow(() -> new IllegalArgumentException("El usuario autor no existe (R3)"));

        tablero.eliminarEtiquetaDeTarjeta(tarjetaId, request.nombreEtiqueta());

        Traza traza = generadorTrazas.crearTrazaEtiqueta(
                autor,
                tablero,
                tarjetaId,
                request.nombreEtiqueta(),
                TipoAccion.ELIMINAR
        );

        tableroRepo.save(tablero);
        trazaRepo.save(traza);
    }
}
