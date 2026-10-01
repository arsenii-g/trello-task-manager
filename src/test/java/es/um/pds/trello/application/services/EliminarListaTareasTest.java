package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.request.EliminarListaTareasRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class EliminarListaTareasTest {

    private EliminarListaTareas servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    private URL urlTableroReal;
    private String idListaAEliminar;
    private final Email EMAIL_AUTOR = new Email("autor.borrado@test.com");

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new EliminarListaTareas(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);

        usuarioRepo.save(new Usuario(EMAIL_AUTOR, "Admin Borrados"));

        Tablero tablero = new Tablero("Tablero Limpieza", EMAIL_AUTOR);
        tablero.añadirLista("Lista que se queda", false,null);
        tablero.añadirLista("Lista para borrar", false,null);

        this.urlTableroReal = tablero.getUrl();
        this.idListaAEliminar = tablero.getListas().get(1).getId();

        tableroRepo.save(tablero);
    }

    @Test
    void debeEliminarLaListaYActualizarRepositorio() {
        EliminarListaTareasRequest request = new EliminarListaTareasRequest(
                urlTableroReal.valor(),
                idListaAEliminar,
                EMAIL_AUTOR.valor()
        );

        servicio.ejecutar(request);

        Tablero tableroActualizado = tableroRepo.findById(urlTableroReal).get();

        boolean existeLaLista = tableroActualizado.getListas().stream()
                .anyMatch(l -> l.getId().equals(idListaAEliminar));

        assertFalse(existeLaLista, "La lista debería haber sido eliminada del tablero");
        assertEquals(1, tableroActualizado.getListas().size(), "El tablero debería tener solo 1 lista ahora");

        List<Traza> trazas = trazaRepo.findByTableroId(urlTableroReal);
        boolean tieneTrazaEliminacion = trazas.stream()
                .anyMatch(t -> t.getAccion() == TipoAccion.ELIMINAR || t.getAccion() == TipoAccion.MODIFICAR);

        assertTrue(tieneTrazaEliminacion, "Debería existir una traza que registre la eliminación de la lista");
    }

    @Test
    void debeLanzarExcepcionSiLaListaNoExiste() {

        EliminarListaTareasRequest request = new EliminarListaTareasRequest(
                urlTableroReal.valor(),
                "id-inexistente",
                EMAIL_AUTOR.valor()
        );

        assertThrows(RuntimeException.class, () -> {
            servicio.ejecutar(request);
        });
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
