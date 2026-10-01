package es.um.pds.trello.application.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.domain.model.*;

import java.util.*;

class FiltrarTarjetasTest {

    private FiltrarTarjetas servicio;
    private TableroRepository tableroRepo;

    private URL urlTablero;
    private final Email EMAIL_DUEÑO = new Email("test@test.com");

    @BeforeEach
    void setUp() {
        tableroRepo = new TableroRepositoryMemoria();
        servicio = new FiltrarTarjetas(tableroRepo);

        Tablero tablero = new Tablero("Tablero Test", EMAIL_DUEÑO);
        tablero.añadirLista("Lista 1", false,null);

        Tarjeta t1 = new TareaTest("Tarjeta 1", "Desc 1");
        t1.asignarEtiqueta(new Etiqueta("Urgente", "#FF0000"));
        tablero.añadirTarjetaALista(tablero.getListas().get(0).getId(), t1);

        Tarjeta t2 = new TareaTest("Tarjeta 2", "Desc 2");
        t2.asignarEtiqueta(new Etiqueta("Baja", "#00FF00"));
        tablero.añadirTarjetaALista(tablero.getListas().get(0).getId(), t2);

        Tarjeta t3 = new TareaTest("Tarjeta 3", "Desc 3");
        t3.asignarEtiqueta(new Etiqueta("Urgente", "#FF0000"));
        t3.asignarEtiqueta(new Etiqueta("Baja", "#00FF00"));
        tablero.añadirTarjetaALista(tablero.getListas().get(0).getId(), t3);

        this.urlTablero = tablero.getUrl();
        tableroRepo.save(tablero);
    }

    @Test
    void debeFiltrarTarjetasPorUnaSolaEtiqueta() {
        TableroDTO resultado = servicio.ejecutar(urlTablero.valor(), List.of("Urgente"));

        assertEquals(1, resultado.listas().size());
        assertEquals(2, resultado.listas().get(0).tarjetas().size(), "Debería haber 2 tarjetas con la etiqueta 'Urgente'");

        assertTrue(resultado.listas().get(0).tarjetas().stream()
                .anyMatch(t -> t.titulo().equals("Tarjeta 1")));
        assertTrue(resultado.listas().get(0).tarjetas().stream()
                .anyMatch(t -> t.titulo().equals("Tarjeta 3")));
    }

    @Test
    void debeFiltrarTarjetasPorMultiplesEtiquetas() {
        TableroDTO resultado = servicio.ejecutar(urlTablero.valor(), List.of("Urgente", "Baja"));

        assertEquals(1, resultado.listas().size());
        assertEquals(3, resultado.listas().get(0).tarjetas().size(), "Deberían aparecer todas porque todas tienen alguna de las etiquetas");
    }

    @Test
    void debeRetornarTodasSiNoHayFiltros() {
        TableroDTO resultado = servicio.ejecutar(urlTablero.valor(), Collections.emptyList());

        assertEquals(3, resultado.listas().get(0).tarjetas().size());
    }

    @Test
    void debeRetornarVacioSiNingunaTarjetaCoincide() {
        TableroDTO resultado = servicio.ejecutar(urlTablero.valor(), List.of("NoExiste"));

        assertEquals(0, resultado.listas().get(0).tarjetas().size());
    }

    private static class TareaTest extends Tarjeta {
        public TareaTest(String titulo, String descripcion) {
            super(titulo, descripcion);
        }
        @Override
        public boolean puedeCompletarse() {
            return true;
        }
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
