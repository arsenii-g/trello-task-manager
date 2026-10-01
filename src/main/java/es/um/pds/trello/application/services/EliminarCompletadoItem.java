package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.request.EliminarCompletadoItemRequest;
import es.um.pds.trello.application.ports.in.IEliminarCompletadoItem;
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
public class EliminarCompletadoItem implements IEliminarCompletadoItem{

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;

    public EliminarCompletadoItem(TableroRepository tableroRepo,
                                  UsuarioRepository usuarioRepo,
                                  TrazaRepository trazaRepo,
                                  GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
    }

    @Transactional
    public void ejecutar(EliminarCompletadoItemRequest request) {

        URL url = new URL(request.urlTablero());
        TarjetaId tarjetaId = new TarjetaId(request.idTarjeta());
        Email emailAutor = new Email(request.emailAutor());

        Tablero tablero = tableroRepo.findById(url)
                .orElseThrow(() -> new IllegalArgumentException("Tablero no encontrado"));

        Usuario autor = usuarioRepo.findById(emailAutor)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado (R3)"));

        String nombreItem = tablero.obtenerNombreItem(tarjetaId, request.indiceItem());

        tablero.cambiarEstadoItemChecklist(tarjetaId, request.indiceItem(), false);
        Traza traza = generadorTrazas.crearTrazaItemChecklist(
                autor,
                tablero,
                tarjetaId,
                nombreItem,
                TipoAccion.MODIFICAR
        );

        tableroRepo.save(tablero);
        trazaRepo.save(traza);
    }
}
