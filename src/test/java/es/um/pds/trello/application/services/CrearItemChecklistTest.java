package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.request.CrearItemChecklistRequest;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;

import java.util.*;

class CrearItemChecklistTest {

    private CrearItemChecklist servicio;
    private TableroRepository tableroRepo;
    private UsuarioRepository usuarioRepo;
    private TrazaRepository trazaRepo;
    private GeneradorTrazas generadorTrazas;

    private URL urlTableroReal;
    private TarjetaId idTarjetaChecklist;
    private final Email EMAIL_AUTOR = new Email("autor.test@example.com");

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        usuarioRepo = new UsuarioRepositoryMemoria();
        trazaRepo = new TrazaRepositoryMemoria();
        generadorTrazas = new GeneradorTrazas();

        servicio = new CrearItemChecklist(tableroRepo, usuarioRepo, trazaRepo, generadorTrazas);

        usuarioRepo.save(new Usuario(EMAIL_AUTOR, "Usuario Test"));

        Tablero tablero = new Tablero("Tablero Checklist", EMAIL_AUTOR);
        tablero.añadirLista("Lista de Tareas", false, null);
        String idLista = tablero.getListas().get(0).getId();

        Checklist checklist = new Checklist("Mi Lista de Compras", "Comida para la semana");
        this.idTarjetaChecklist = checklist.getId();
        tablero.añadirTarjetaALista(idLista, checklist);

        this.urlTableroReal = tablero.getUrl();
        tableroRepo.save(tablero);
    }

    @Test
    void debeAñadirItemAChecklistYRegistrarTraza() {
        String textoItem = "Comprar manzanas";
        CrearItemChecklistRequest request = new CrearItemChecklistRequest(
                urlTableroReal.valor(),
                idTarjetaChecklist.valor(),
                textoItem,
                EMAIL_AUTOR.valor()
        );

        servicio.ejecutar(request);

        Tablero tableroActualizado = tableroRepo.findById(urlTableroReal).get();

        Tarjeta tarjeta = tableroActualizado.getListas().get(0).getTarjetas().stream()
                .filter(t -> t.getId().equals(idTarjetaChecklist))
                .findFirst()
                .orElseThrow();

        assertTrue(tarjeta instanceof Checklist, "La tarjeta debería ser de tipo Checklist");
        Checklist checklistActualizada = (Checklist) tarjeta;

        assertEquals(1, checklistActualizada.getItems().size(), "La checklist debería tener 1 ítem");
        assertEquals(textoItem, checklistActualizada.getItems().get(0).descripcion());
        assertFalse(checklistActualizada.getItems().get(0).estado(), "El ítem debería estar pendiente por defecto");

        List<Traza> trazas = trazaRepo.findByTableroId(urlTableroReal);
        boolean tieneTraza = trazas.stream()
                .anyMatch(t -> t.getAccion() == TipoAccion.MODIFICAR && t.getDetalles().contains(textoItem));

        assertTrue(tieneTraza, "Debería existir una traza con la descripción del ítem añadido");
    }

    @Test
    void debeLanzarExcepcionSiLaTarjetaNoEsChecklist() {
        Tablero tablero = tableroRepo.findById(urlTableroReal).get();
        Tarea tareaNormal = new Tarea("Tarea Normal", "No acepta items");
        TarjetaId idTareaNormal = tareaNormal.getId();
        tablero.añadirTarjetaALista(tablero.getListas().get(0).getId(), tareaNormal);
        tableroRepo.save(tablero);

        CrearItemChecklistRequest request = new CrearItemChecklistRequest(
                urlTableroReal.valor(),
                idTareaNormal.valor(),
                "Intento fallido",
                EMAIL_AUTOR.valor()
        );

        assertThrows(IllegalArgumentException.class, () -> {
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
