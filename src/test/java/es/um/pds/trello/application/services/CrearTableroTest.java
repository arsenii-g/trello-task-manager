package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.request.CrearTableroRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.Email;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.TipoAccion;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.URL;
import es.um.pds.trello.domain.model.Usuario;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class CrearTableroTest {

    private CrearTablero servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new CrearTablero(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);
    }

    @Test
    void debeCrearTableroYPersistirEnMemoria() {

        String emailStr = "propietario@test.com";
        CrearTableroRequest request = new CrearTableroRequest(
                emailStr,
                "Juan Perez",
                "Mi Tablero de Proyecto"
        );

        TableroDTO resultado = servicio.ejecutar(request);

        assertNotNull(resultado.url(), "La URL del tablero no debería ser nula");
        assertEquals("Mi Tablero de Proyecto", resultado.titulo());

        Email emailBusqueda = new Email(emailStr);
        assertTrue(usuarioRepo.findById(emailBusqueda).isPresent(),
                "El usuario dueño debería estar guardado en el repositorio");

        URL urlDelTablero = new URL(resultado.url());
        Optional<Tablero> tableroGuardado = tableroRepo.findById(urlDelTablero);
        assertTrue(tableroGuardado.isPresent(), "El tablero debería existir en el repositorio");
        assertEquals(emailStr, tableroGuardado.get().getDueñoId().valor());

        List<Traza> trazasDelTablero = trazaRepo.findByTableroId(urlDelTablero);
        assertEquals(1, trazasDelTablero.size(), "Debería haber exactamente 1 traza para el tablero creado");

        assertEquals(TipoAccion.CREAR, trazasDelTablero.get(0).getAccion(),
                "La traza debería ser de tipo CREAR");
    }

    private static class TableroRepositoryMemoria implements TableroRepository {
        private final Map<URL, Tablero> storage = new HashMap<>();

        @Override
        public void save(Tablero tablero) {
            storage.put(tablero.getUrl(), tablero);
        }

        @Override
        public Optional<Tablero> findById(URL url) {
            return Optional.ofNullable(storage.get(url));
        }
    }

    private static class UsuarioRepositoryMemoria implements UsuarioRepository {
        private final Map<Email, Usuario> storage = new HashMap<>();

        @Override
        public Optional<Usuario> findById(Email email) {
            return Optional.ofNullable(storage.get(email));
        }

        @Override
        public Usuario save(Usuario usuario) {
            storage.put(usuario.getId(), usuario);
            return usuario;
        }
    }

    private static class TrazaRepositoryMemoria implements TrazaRepository {
        private final List<Traza> storage = new ArrayList<>();

        @Override
        public Traza save(Traza traza) {
            storage.add(traza);
            return traza;
        }

        @Override
        public Optional<Traza> findById(es.um.pds.trello.domain.model.TrazaId id) {
            return storage.stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<Traza> findByTableroId(URL urlTablero) {
            return storage.stream()
                    .filter(t -> t.getTableroId().equals(urlTablero))
                    .toList();
        }
    }
}