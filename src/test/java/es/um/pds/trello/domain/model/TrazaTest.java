package es.um.pds.trello.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class TrazaTest {

    @Test
    void testCrearTraza_DeberiaInicializarDatosCorrectamente() {

        Email autor = new Email("author@example.com");
        URL tableroUrl = new URL("https://trello.com/b/123");

        Traza traza = new Traza(autor, tableroUrl, TipoObjeto.TARJETA, TipoAccion.CREAR, "Se creó la tarea");

        assertNotNull(traza.getId(), "La traza debe generar un ID automáticamente");
        assertNotNull(traza.getFecha(), "La traza debe registrar la fecha actual");
        assertEquals(autor, traza.getAutor());
        assertEquals(TipoAccion.CREAR, traza.getAccion());

        assertTrue(traza.getFecha().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testCrearTraza_SinDatosObligatorios_DeberiaLanzarExcepcion() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Traza(null, null, null, null, "error");
        });
    }

    @Test
    void testTrazaConObjetoAfectado_DeberiaGuardarIdDeTarjeta() {

        Email autor = new Email("user@example.com");
        URL tableroUrl = new URL("https://trello.com/b/456");
        TarjetaId idTarjeta = new TarjetaId("T-999");

        Traza traza = new Traza(autor, tableroUrl, idTarjeta, TipoObjeto.TARJETA, TipoAccion.MOVER, "Movida a Done");

        assertNotNull(traza.getObjetoAfectadoId());
        assertEquals("T-999", traza.getObjetoAfectadoId().valor());
        assertEquals(TipoObjeto.TARJETA, traza.getTipoObjeto());
    }
}