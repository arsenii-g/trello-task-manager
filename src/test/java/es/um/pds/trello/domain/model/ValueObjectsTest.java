package es.um.pds.trello.domain.model;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    void testEtiquetaColorInvalido_DeberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Etiqueta("Urgente", "rojo"));
        assertThrows(IllegalArgumentException.class, () -> new Etiqueta("Urgente", "#123"));
        assertThrows(IllegalArgumentException.class, () -> new Etiqueta("Urgente", "FF0000"));
    }

    @Test
    void testUrlFormatoCorrecto() {
        assertDoesNotThrow(() -> new URL("https://trello.com/tableros/mi-tablero"));

        assertThrows(IllegalArgumentException.class, () -> new URL("esto-no-es-url"));
    }

    @Test
    void testEmail_FormatosValidos_NoDeberianLanzarExcepcion() {
        assertDoesNotThrow(() -> new Email("usuario@um.es"));
        assertDoesNotThrow(() -> new Email("test.pro@gmail.com"));
        assertDoesNotThrow(() -> new Email("admin_123@trello.org"));
    }

}