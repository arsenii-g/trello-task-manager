package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

class CrearDesdePlantillaTest {

    private CrearDesdePlantilla servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        servicio = new CrearDesdePlantilla(tableroRepo, usuarioRepo, trazaRepo, new GeneradorTrazas());
    }

    @Test
    void debeCrearTableroDesdePlantillaYAML() {
        String yaml = """
                titulo: "Plantilla de Proyecto"
                listas:
                  - titulo: "Por hacer"
                    esListaCompletadas: false
                    limite: 5
                    tarjetas:
                      - titulo: "Configurar entorno"
                        descripcion: "Instalar JDK y Maven"
                        tipo: "TAREA"
                        etiquetas: ["Urgente", "Setup"]
                      - titulo: "Checklist Inicial"
                        descripcion: "Pasos básicos"
                        tipo: "CHECKLIST"
                  - titulo: "Hecho"
                    esListaCompletadas: true
                    limite: null
                    tarjetas: []
                """;

        TableroDTO resultado = servicio.ejecutar("dueno@test.com", "Dueño Test", yaml);

        assertNotNull(resultado);
        assertEquals("Plantilla de Proyecto", resultado.titulo());
        assertEquals(2, resultado.listas().size());

        var listaPorHacer = resultado.listas().get(0);
        assertEquals("Por hacer", listaPorHacer.titulo());
         assertEquals(2, listaPorHacer.tarjetas().size());
         assertEquals(5, listaPorHacer.limiteTarjetas());

        var t1 = listaPorHacer.tarjetas().get(0);
        assertEquals("Configurar entorno", t1.titulo());
        assertEquals("Tarea", t1.tipo());
        assertEquals(Set.of("Urgente|#808080", "Setup|#808080"), new HashSet<>(t1.etiquetas()));

        var t2 = listaPorHacer.tarjetas().get(1);
        assertEquals("Checklist Inicial", t2.titulo());
        assertEquals("Checklist", t2.tipo());
    }

    @Test
    void debeFallarSiSeExcedeElLimiteEnPlantilla() {
        String yaml = """
                titulo: "Proyecto Limitado"
                listas:
                  - titulo: "Limitada"
                    esListaCompletadas: false
                    limite: 1
                    tarjetas:
                      - titulo: "T1"
                        descripcion: "D1"
                        tipo: "TAREA"
                      - titulo: "T2"
                        descripcion: "D2"
                        tipo: "TAREA"
                """;

        assertThrows(RuntimeException.class, () ->
            servicio.ejecutar("dueno@test.com", "Dueño Test", yaml)
        );
    }

    private static class TableroRepositoryMemoria implements TableroRepository {
        private final Map<URL, Tablero> storage = new HashMap<>();
        public void save(Tablero t) { storage.put(t.getUrl(), t); }
        public Optional<Tablero> findById(URL url) { return Optional.ofNullable(storage.get(url)); }
    }

    private static class UsuarioRepositoryMemoria implements UsuarioRepository {
        private final Map<Email, Usuario> storage = new HashMap<>();
        public Usuario save(Usuario u) { storage.put(u.getId(), u); return u; }
        public Optional<Usuario> findById(Email email) { return Optional.ofNullable(storage.get(email)); }
    }

    private static class TrazaRepositoryMemoria implements TrazaRepository {
        public Traza save(Traza t) { return t; }
        public Optional<Traza> findById(TrazaId id) { return Optional.empty(); }
        public List<Traza> findByTableroId(URL urlTablero) { return List.of(); }
    }
}
