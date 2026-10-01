package es.um.pds.trello.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChecklistTest {

    @Test
    void testReglaR6_CompletarTodosLosItemsMarcaTarjetaComoHecha() {
        Checklist checklist = new Checklist("Lista Compra", "Supermercado");
        checklist.añadirItem("Leche");

        assertFalse(checklist.isCompletada(), "No debería estar completa con ítems pendientes");

        checklist.cambiarEstadoItem(0, true);

        assertTrue(checklist.isCompletada(), "La tarjeta debería marcarse como completada automáticamente");
    }

    @Test
    void testReglaR9_AñadirItemATarjetaCompletadaLaVuelveAPendiente() {
        Checklist checklist = new Checklist("Viaje", "Maletas");
        checklist.añadirItem("Pasaporte");
        checklist.cambiarEstadoItem(0, true);
        assertTrue(checklist.isCompletada());

        checklist.añadirItem("Cargador");

        assertFalse(checklist.isCompletada(), "Al añadir un ítem nuevo, la tarjeta debe volver a estar pendiente");
    }
}