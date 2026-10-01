package es.um.pds.trello.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.request.CrearTarjetaRequest;
import es.um.pds.trello.application.ports.in.ICrearTarjeta;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.Checklist;
import es.um.pds.trello.domain.model.Email;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.Tarea;
import es.um.pds.trello.domain.model.Tarjeta;
import es.um.pds.trello.domain.model.TarjetaId;
import es.um.pds.trello.domain.model.TipoAccion;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.URL;
import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.domain.services.GeneradorTrazas;

@Service
public class CrearTarjeta implements ICrearTarjeta{

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;

    public CrearTarjeta(TableroRepository tableroRepo,
                        UsuarioRepository usuarioRepo,
                        TrazaRepository trazaRepo,
                        GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
    }

    @Transactional
    public void ejecutar(CrearTarjetaRequest request) {

        URL url = new URL(request.urlTablero());
        Email email = new Email(request.emailAutor());

        Tablero tablero = tableroRepo.findById(url)
                .orElseThrow(() -> new IllegalArgumentException("Tablero no encontrado"));

        Usuario autor = usuarioRepo.findById(email)
                .orElseGet(() -> usuarioRepo.save(new Usuario(email, email.valor().split("@")[0])));

        Tarjeta tarjetaProcesada;
        TipoAccion accion;

        if(request.idTarjeta() != null && !request.idTarjeta().isBlank()) {
        	TarjetaId id = new TarjetaId(request.idTarjeta());
        	tablero.modificarDatosTarjeta(id, request.titulo(), request.descripcion());

        	tarjetaProcesada = buscarTarjetaEnTablero(tablero, id);
        	accion = TipoAccion.MODIFICAR;
        }
        else {
        	if("CHECKLIST".equalsIgnoreCase(request.tipoTarjeta())) {
        		tarjetaProcesada = new Checklist(request.titulo(), request.descripcion());
            }
        	else {
            	tarjetaProcesada = new Tarea(request.titulo(), request.descripcion());
            }
        	tablero.añadirTarjetaALista(request.idLista(), tarjetaProcesada);
        	accion = TipoAccion.CREAR;
        }

        tableroRepo.save(tablero);

        Traza traza = generadorTrazas.crearTrazaTarjeta(
                autor,
                tablero,
                tarjetaProcesada.getId(),
                accion
        );

        trazaRepo.save(traza);
    }

    private Tarjeta buscarTarjetaEnTablero(Tablero tablero, TarjetaId id) {
    	return tablero.getListas().stream()
    			.flatMap(l -> l.getTarjetas().stream())
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));
    }
}