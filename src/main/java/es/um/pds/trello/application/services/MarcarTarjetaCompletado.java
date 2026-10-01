package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.request.MarcarTarjetaCompletadoRequest;
import es.um.pds.trello.application.ports.in.IMarcarTarjetaCompletado;
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
public class MarcarTarjetaCompletado implements IMarcarTarjetaCompletado{

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;

    public MarcarTarjetaCompletado(TableroRepository tableroRepo,
                                   UsuarioRepository usuarioRepo,
                                   TrazaRepository trazaRepo,
                                   GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
    }

    @Transactional
    public void ejecutar(MarcarTarjetaCompletadoRequest request) {

        URL url = new URL(request.urlTablero());
        TarjetaId tarjetaId = new TarjetaId(request.idTarjeta());

        Tablero tablero = tableroRepo.findById(url)
                .orElseThrow(() -> new IllegalArgumentException("Tablero no encontrado"));

        Email email = new Email(request.emailAutor());
        Usuario autor = usuarioRepo.findById(email)
                .orElseGet(() -> usuarioRepo.save(new Usuario(email, email.valor().split("@")[0])));

        tablero.cambiarEstadoTarjeta(tarjetaId, true);

        Traza traza = generadorTrazas.crearTrazaTarjeta(
                autor,
                tablero,
                tarjetaId,
                TipoAccion.MODIFICAR
        );

        tableroRepo.save(tablero);
        trazaRepo.save(traza);
    }
}
