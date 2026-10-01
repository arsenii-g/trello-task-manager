package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.request.MarcarTarjetaCompletadoRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class MarcarTarjetaCompletadoTest {

    private MarcarTarjetaCompletado servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    private URL urlTablero;
    private TarjetaId idTarjeta;
    private final Email EMAIL_TEST = new Email("user@test.com");

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new MarcarTarjetaCompletado(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);

        usuarioRepo.save(new Usuario(EMAIL_TEST, "Tester"));

        Tablero tablero = new Tablero("Tablero Test", EMAIL_TEST);

        tablero.añadirLista("Pendientes", false, null);

        tablero.añadirListaCompletadas("Hecho");

        Tarjeta tarjeta = new Tarea("Tarea 1", "Descripción");
        this.idTarjeta = tarjeta.getId();

        tablero.añadirTarjetaALista(tablero.getListas().get(0).getId(), tarjeta);

        this.urlTablero = tablero.getUrl();
        tableroRepo.save(tablero);
    }

    @Test
    void debeCompletarTarjetaYMoverlaAListaHecho() {
        MarcarTarjetaCompletadoRequest request = new MarcarTarjetaCompletadoRequest(
                urlTablero.valor(),
                idTarjeta.valor(),
                EMAIL_TEST.valor()
        );

        servicio.ejecutar(request);

        Tablero tableroActualizado = tableroRepo.findById(urlTablero).get();

        Tarjeta tarjetaFinal = tableroActualizado.getListas().stream()
                .flatMap(l -> l.getTarjetas().stream())
                .filter(t -> t.getId().equals(idTarjeta))
                .findFirst()
                .orElseThrow();

        assertTrue(tarjetaFinal.isCompletada(), "La tarjeta debería estar completada");

        ListaTareas listaHecho = tableroActualizado.getListas().get(1);
        assertTrue(listaHecho.getTarjetas().contains(tarjetaFinal), "La tarjeta debería haberse movido a 'Hecho'");
    }

    private static class TableroRepositoryMemoria implements TableroRepository {
        private final Map<URL, Tablero> datos = new HashMap<>();
        @Override public void save(Tablero t) { datos.put(t.getUrl(), t); }
        @Override public Optional<Tablero> findById(URL url) { return Optional.ofNullable(datos.get(url)); }
    }

    private static class UsuarioRepositoryMemoria implements UsuarioRepository {
        private final Map<Email, Usuario> datos = new HashMap<>();
        @Override public Optional<Usuario> findById(Email e) { return Optional.ofNullable(datos.get(e)); }
        @Override public Usuario save(Usuario u) { datos.put(u.getId(), u); return u; }
    }

    private static class TrazaRepositoryMemoria implements TrazaRepository {
        private final List<Traza> datos = new ArrayList<>();
        @Override public Traza save(Traza t) { datos.add(t); return t; }
        @Override public Optional<Traza> findById(TrazaId id) { return Optional.empty(); }
        @Override public List<Traza> findByTableroId(URL url) {
            return datos.stream().filter(t -> t.getTableroId().equals(url)).toList();
        }
    }
}