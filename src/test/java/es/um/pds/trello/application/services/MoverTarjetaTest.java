package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.request.MoverTarjetasRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class MoverTarjetaTest {

    private MoverTarjeta servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    private URL urlTablero;
    private final Email EMAIL_AUTOR = new Email("autor@test.com");
    private String idListaOrigen;
    private String idListaDestino;
    private TarjetaId idTarjeta;

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new MoverTarjeta(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);

        usuarioRepo.save(new Usuario(EMAIL_AUTOR, "Pepe"));

        Tablero tablero = new Tablero("Tablero Movimientos", EMAIL_AUTOR);
        tablero.añadirLista("Lista Origen", false,null);
        tablero.añadirLista("Lista Destino", false,null);

        this.urlTablero = tablero.getUrl();
        this.idListaOrigen = tablero.getListas().get(0).getId();
        this.idListaDestino = tablero.getListas().get(1).getId();

        Tarjeta tarjeta = new Tarea("Tarea a mover", "Descripción");
        this.idTarjeta = tarjeta.getId();
        tablero.añadirTarjetaALista(idListaOrigen, tarjeta);

        tableroRepo.save(tablero);
    }

    @Test
    void debeMoverTarjetaCorrectamenteEntreListas() {
        MoverTarjetasRequest request = new MoverTarjetasRequest(
                urlTablero.valor(),
                idTarjeta.valor(),
                idListaOrigen,
                idListaDestino,
                EMAIL_AUTOR.valor()
        );

        servicio.ejecutar(request);

        Tablero tableroActualizado = tableroRepo.findById(urlTablero).get();

        ListaTareas listaOrigen = tableroActualizado.getListas().stream()
                .filter(l -> l.getId().equals(idListaOrigen)).findFirst().get();
        ListaTareas listaDestino = tableroActualizado.getListas().stream()
                .filter(l -> l.getId().equals(idListaDestino)).findFirst().get();

        assertTrue(listaOrigen.getTarjetas().isEmpty(), "La lista de origen debería estar vacía");
        assertEquals(1, listaDestino.getTarjetas().size(), "La lista de destino debería tener la tarjeta");
        assertEquals(idTarjeta, listaDestino.getTarjetas().get(0).getId());

        List<Traza> trazas = trazaRepo.findByTableroId(urlTablero);

        boolean existeTrazaMover = trazas.stream()
                .anyMatch(t -> t.getAccion() == TipoAccion.MOVER || t.getAccion() == TipoAccion.MODIFICAR);

        assertTrue(existeTrazaMover, "Debería existir una traza que registre el movimiento");
    }

    @Test
    void debeMoverTarjetaSiElTableroEstaBloqueado() {
        Tablero tablero = tableroRepo.findById(urlTablero).get();
        tablero.bloquearTablero();
        tableroRepo.save(tablero);

        MoverTarjetasRequest request = new MoverTarjetasRequest(
                urlTablero.valor(), idTarjeta.valor(), idListaOrigen, idListaDestino, EMAIL_AUTOR.valor()
        );

        assertDoesNotThrow(() -> servicio.ejecutar(request));

        Tablero tableroActualizado = tableroRepo.findById(urlTablero).orElseThrow();
        ListaTareas listaOrigen = tableroActualizado.getListas().stream()
                .filter(l -> l.getId().equals(idListaOrigen)).findFirst().orElseThrow();
        ListaTareas listaDestino = tableroActualizado.getListas().stream()
                .filter(l -> l.getId().equals(idListaDestino)).findFirst().orElseThrow();

        assertTrue(tableroActualizado.getEstado().esBloqueado());
        assertTrue(listaOrigen.getTarjetas().isEmpty());
        assertEquals(1, listaDestino.getTarjetas().size());
        assertEquals(idTarjeta, listaDestino.getTarjetas().get(0).getId());
    }

    private static class TableroRepositoryMemoria implements TableroRepository {
        private final Map<URL, Tablero> storage = new HashMap<>();
        @Override public void save(Tablero t) { storage.put(t.getUrl(), t); }
        @Override public Optional<Tablero> findById(URL url) { return Optional.ofNullable(storage.get(url)); }
    }

    private static class UsuarioRepositoryMemoria implements UsuarioRepository {
        private final Map<Email, Usuario> storage = new HashMap<>();
        @Override public Optional<Usuario> findById(Email e) { return Optional.ofNullable(storage.get(e)); }
        @Override public Usuario save(Usuario u) { storage.put(u.getId(), u); return u; }
    }

    private static class TrazaRepositoryMemoria implements TrazaRepository {
        private final List<Traza> storage = new ArrayList<>();
        @Override public Traza save(Traza t) { storage.add(t); return t; }
        @Override public Optional<Traza> findById(TrazaId id) { return Optional.empty(); }
        @Override public List<Traza> findByTableroId(URL url) {
            return storage.stream().filter(t -> t.getTableroId().equals(url)).toList();
        }
    }
}
