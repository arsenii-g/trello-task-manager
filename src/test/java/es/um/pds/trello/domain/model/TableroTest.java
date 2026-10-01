package es.um.pds.trello.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableroTest {

    private Tablero tablero;
    private Email dueñoEmail;

    @BeforeEach
    void setUp() {
        dueñoEmail = new Email("estudiante@um.es");
        tablero = new Tablero("Proyecto PDS", dueñoEmail);
    }

    @Test
    void testReglaR1_BloqueoImpideModificaciones() {
        tablero.bloquearTablero();
        assertThrows(IllegalStateException.class, () -> {
            tablero.añadirLista("Nueva Lista", false,null);
        });
    }

    @Test
    void testReglaR4_GeneracionUrlUnica() {
        assertNotNull(tablero.getUrl());
        assertTrue(tablero.getUrl().valor().contains("trello.com"));
    }

    @Test
    void testMovimientoManualEntreListas() {
        tablero.añadirLista("TODO", false,null);
        tablero.añadirLista("DOING", false,null);

        String idTodo = tablero.getListas().get(0).getId();
        String idDoing = tablero.getListas().get(1).getId();

        Tarea tarea = new Tarea("Tarea 1", "Hacer tests");
        tablero.añadirTarjetaALista(idTodo, tarea);

        tablero.moverTarjeta(tarea.getId(), idTodo, idDoing);

        assertTrue(tablero.getListas().get(0).getTarjetas().isEmpty());
        assertEquals(1, tablero.getListas().get(1).getTarjetas().size());
    }

    @Test
    void testReglaR2_CambiarEstadoTarjeta_MueveAutomaticamenteAListaCompletadas() {
        tablero.añadirLista("Pendientes", false,null);
        tablero.añadirListaCompletadas("Terminado");

        String idPendientes = tablero.getListas().get(0).getId();
        Tarea tarea = new Tarea("Hacer la compra", "Urgente");
        tablero.añadirTarjetaALista(idPendientes, tarea);

        tablero.cambiarEstadoTarjeta(tarea.getId(), true);

        assertEquals(0, tablero.getListas().get(0).getTarjetas().size(), "Debe salir de la lista Pendientes");
        assertEquals(1, tablero.getListas().get(1).getTarjetas().size(), "Debe entrar en la lista Terminado");
        assertTrue(tablero.getListas().get(1).getTarjetas().get(0).isCompletada());
    }
}