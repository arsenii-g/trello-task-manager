package es.um.pds.trello.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListaTareasTest {

    @Test
    void testReglaR2_NoPermiteTarjetasPendientesEnListaDeCompletadas() {
        Tablero tablero = new Tablero("Tablero Test", new Email("test@test.com"));
        ListaTareas listaHechos = new ListaTareas(tablero, "DONE", 1, true,null);
        Tarea tareaPendiente = new Tarea("Hacer ejercicio", "Gimnasio");

        assertThrows(IllegalStateException.class, () -> {
            listaHechos.añadirTarjeta(tareaPendiente);
        }, "Debería impedir añadir una tarea que no esté marcada como completada");
    }
}