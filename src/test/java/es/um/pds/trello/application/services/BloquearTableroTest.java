package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.request.BloquearTableroRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class BloquearTableroTest {

    private BloquearTablero servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    private URL urlTablero;
    private final Email EMAIL_DUEÑO = new Email("dueno@test.com");
    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new BloquearTablero(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);

        usuarioRepo.save(new Usuario(EMAIL_DUEÑO, "Admin"));

        Tablero tablero = new Tablero("Tablero para bloquear", EMAIL_DUEÑO);
        this.urlTablero = tablero.getUrl();

        tableroRepo.save(tablero);
    }

    @Test
    void debeCambiarEstadoABloqueadoYRegistrarTraza() {
        BloquearTableroRequest request = new BloquearTableroRequest(
                urlTablero.valor(),
                EMAIL_DUEÑO.valor()
        );

        servicio.ejecutar(request);

        Tablero tableroActualizado = tableroRepo.findById(urlTablero)
                .orElseThrow(() -> new IllegalStateException("El tablero debería existir"));

        assertTrue(tableroActualizado.getEstado().esBloqueado(),
                "El estado del tablero debería ser BLOQUEADO tras ejecutar el servicio");

        List<Traza> trazas = trazaRepo.findByTableroId(urlTablero);
        boolean tieneTrazaBloqueo = trazas.stream()
                .anyMatch(t -> t.getAccion() == TipoAccion.BLOQUEAR || t.getAccion() == TipoAccion.MODIFICAR);

        assertTrue(tieneTrazaBloqueo, "Debería existir una traza que registre el bloqueo del tablero");
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
