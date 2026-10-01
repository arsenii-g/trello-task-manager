package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.request.CrearTarjetaRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class CrearTarjetaTest {

    private CrearTarjeta servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    private URL urlTableroReal;
    private final Email EMAIL_AUTOR = new Email("autor@test.com");

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new CrearTarjeta(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);

        Usuario autor = new Usuario(EMAIL_AUTOR, "Pepe");
        usuarioRepo.save(autor);

        Tablero tablero = new Tablero("Tablero de Prueba", EMAIL_AUTOR);
        tablero.añadirLista("Pendientes", false,null);

        this.urlTableroReal = tablero.getUrl();

        tableroRepo.save(tablero);
    }

    @Test
    void debeCrearUnaTareaNormalEnElTablero() {
        Tablero tableroExistente = tableroRepo.findById(urlTableroReal)
                .orElseThrow(() -> new IllegalStateException("Tablero no encontrado en el repo"));

        String idListaReal = tableroExistente.getListas().get(0).getId();

        CrearTarjetaRequest request = new CrearTarjetaRequest(
                urlTableroReal.valor(),
                idListaReal,
                null,
                "Comprar leche",
                "Ir al súper a las 10",
                EMAIL_AUTOR.valor(),
                "TAREA"
        );

        servicio.ejecutar(request);

        Tablero tableroActualizado = tableroRepo.findById(urlTableroReal).get();
        ListaTareas lista = tableroActualizado.getListas().get(0);
        assertEquals(1, lista.getTarjetas().size());

        Tarjeta tarjetaCreada = lista.getTarjetas().get(0);
        assertEquals("Comprar leche", tarjetaCreada.getTitulo());

        List<Traza> trazas = trazaRepo.findByTableroId(urlTableroReal);
        assertEquals(1, trazas.size());
    }

    @Test
    void debeCrearUnaTarjetaDeTipoChecklist() {
        Tablero tableroExistente = tableroRepo.findById(urlTableroReal).get();
        String idListaReal = tableroExistente.getListas().get(0).getId();

        CrearTarjetaRequest request = new CrearTarjetaRequest(
                urlTableroReal.valor(),
                idListaReal,
                null,
                "Lista de la compra",
                "Cosas necesarias",
                EMAIL_AUTOR.valor(),
                "CHECKLIST"
        );

        servicio.ejecutar(request);

        Tablero tablero = tableroRepo.findById(urlTableroReal).get();
        Tarjeta tarjeta = tablero.getListas().get(0).getTarjetas().get(0);

        assertTrue(tarjeta instanceof Checklist, "La tarjeta debería ser una instancia de Checklist");
    }

    @Test
    void debeActualizarUnaTarjetaExistenteEnLugarDeCrearUnaNueva() {
    	Tablero tablero = tableroRepo.findById(urlTableroReal).get();
    	String idLista = tablero.getListas().get(0).getId();
    	Tarea tareaOriginal = new Tarea("Original", "Descripción antigua");
    	tablero.añadirTarjetaALista(idLista, tareaOriginal);
    	tableroRepo.save(tablero);

    	String idTarjeta = tareaOriginal.getId().valor();

    	CrearTarjetaRequest request = new CrearTarjetaRequest(
    			urlTableroReal.valor(),
    			idLista,
    			idTarjeta,
    			"Título nuevo",
    			"Descripción nueva",
    			EMAIL_AUTOR.valor(),
    			"TAREA"
    	);

    	servicio.ejecutar(request);

    	Tablero tableroPost = tableroRepo.findById(urlTableroReal).get();
    	assertEquals(1, tableroPost.getListas().get(0).getTarjetas().size());

    	Tarjeta editada = tableroPost.getListas().get(0).getTarjetas().get(0);
    	assertEquals("Título nuevo", editada.getTitulo());
    	assertEquals("Descripción nueva", editada.getDescripcion());
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