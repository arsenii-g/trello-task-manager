package es.um.pds.trello.domain.model;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.um.pds.trello.domain.services.GeneradorTrazas;

class GeneradorTrazasTest {

    private GeneradorTrazas generador;
    private Usuario autor;
    private Tablero tablero;

    @BeforeEach
    void setUp() {
        generador = new GeneradorTrazas();
        autor = new Usuario("user@example.com", "Pepe");
        tablero = new Tablero("Proyecto PDS", new Email("propietario@um.es"));
    }

    @Test
    void testCrearTrazaTablero_DeberiaFormatearDetallesCorrectamente() {

        Traza traza = generador.crearTrazaTablero(autor, tablero, TipoAccion.BLOQUEAR);

        assertEquals(TipoObjeto.TABLERO, traza.getTipoObjeto());
        assertEquals(TipoAccion.BLOQUEAR, traza.getAccion());
        assertTrue(traza.getDetalles().contains("Pepe"));
        assertTrue(traza.getDetalles().contains("Proyecto PDS"));
    }

    @Test
    void testCrearTrazaMovimiento_DeberiaIncluirOrigenYDestino() {

        TarjetaId tarjetaId = new TarjetaId("T-101");

        Traza traza = generador.crearTrazaMovimiento(autor, tablero, tarjetaId, "Pendiente", "Terminado");

        assertEquals(TipoAccion.MOVER, traza.getAccion());
        assertEquals(tarjetaId, traza.getObjetoAfectadoId());
        assertTrue(traza.getDetalles().contains("Pendiente -> Terminado"));
    }

    @Test
    void testCrearTrazaItemChecklist_DeberiaReflejarEstadoCompletado() {

        TarjetaId tarjetaId = new TarjetaId("T-101");

        Traza traza = generador.crearTrazaItemChecklist(autor, tablero, tarjetaId, "Comprar pan", TipoAccion.COMPLETAR);

        assertEquals(TipoObjeto.ITEM_CHECKLIST, traza.getTipoObjeto());
        assertTrue(traza.getDetalles().contains("marcado como completado"));
        assertTrue(traza.getDetalles().contains("Comprar pan"));
    }

    @Test
    void testCrearTrazaEtiqueta_DeberiaDiferenciarAccionTexto() {

        TarjetaId tarjetaId = new TarjetaId("T-101");

        Traza traza = generador.crearTrazaEtiqueta(autor, tablero, tarjetaId, "Urgente", TipoAccion.ELIMINAR);

        assertTrue(traza.getDetalles().contains("eliminado"), "El texto debería decir 'eliminado'");
        assertEquals(TipoObjeto.ETIQUETA, traza.getTipoObjeto());
    }
}
