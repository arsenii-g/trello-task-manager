package es.um.pds.trello.domain.model;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void testCrearUsuario_DeberiaAsignarDatosCorrectamente() {
        Email email = new Email("user@example.com");
        Usuario usuario = new Usuario(email, "Pepito");

        assertEquals(email, usuario.getId());
        assertEquals("Pepito", usuario.getNombre());
    }

    @Test
    void testConstructorUsuario_NoDeberiaPermitirEmailNulo() {

        assertThrows(IllegalArgumentException.class, () -> new Usuario((Email) null, "Pepito"));
    }

    @Test
    void testCambiarNombre_ValidacionDeNegocio() {
        Usuario usuario = new Usuario("user@example.com", "Antiguo");

        usuario.cambiarNombre("Nuevo");
        assertEquals("Nuevo", usuario.getNombre());

        assertThrows(IllegalArgumentException.class, () -> usuario.cambiarNombre("  "));
    }
}