package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.domain.model.*;

import java.util.*;

class AccederTableroTest {

    private AccederTablero servicio;
    private TableroRepository tableroRepo;

    private URL urlExistente;
    private final Email EMAIL_DUEÑO = new Email("dueno@test.com");

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        servicio = new AccederTablero(tableroRepo);

        Tablero tablero = new Tablero("Tablero de Proyecto", EMAIL_DUEÑO);
        this.urlExistente = tablero.getUrl();
        tableroRepo.save(tablero);
    }

    @Test
    void debeRetornarTableroDTOCuandoLaUrlExiste() {
        TableroDTO resultado = servicio.ejecutar(urlExistente.valor());

        assertNotNull(resultado, "El DTO resultado no debería ser nulo");
        assertEquals("Tablero de Proyecto", resultado.titulo());
        assertEquals(urlExistente.valor(), resultado.url());
    }

    @Test
    void debeLanzarExcepcionCuandoTableroNoExiste() {
        String urlInvalida = "https://trello.com/tableros/" + UUID.randomUUID().toString();

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            servicio.ejecutar(urlInvalida);
        });

        assertTrue(excepcion.getMessage().contains("no existe"),
                "El mensaje de error debería indicar que el tablero no existe");
    }

    @Test
    void debeLanzarExcepcionSiLaUrlTieneFormatoInvalido() {
        String urlMalFormada = "url-invalida-sin-protocolo";

        assertThrows(IllegalArgumentException.class, () -> {
            servicio.ejecutar(urlMalFormada);
        });
    }

    private static class TableroRepositoryMemoria implements TableroRepository {
        private final Map<URL, Tablero> storage = new HashMap<>();

        @Override
        public void save(Tablero t) {
            storage.put(t.getUrl(), t);
        }

        @Override
        public Optional<Tablero> findById(URL url) {
            return Optional.ofNullable(storage.get(url));
        }
    }
}