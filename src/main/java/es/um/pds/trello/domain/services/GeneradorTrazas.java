package es.um.pds.trello.domain.services;

import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.TarjetaId;
import es.um.pds.trello.domain.model.TipoAccion;
import es.um.pds.trello.domain.model.TipoObjeto;
import es.um.pds.trello.domain.model.Traza;
import es.um.pds.trello.domain.model.Usuario;

public class GeneradorTrazas {

    public Traza crearTrazaTablero(Usuario autor, Tablero tablero, TipoAccion accion) {
        return new Traza(
                autor.getId(),
                tablero.getUrl(),
                TipoObjeto.TABLERO,
                accion,
                String.format("Acción %s realizada por %s sobre el tablero: %s",
                                accion, autor.getNombre(), tablero.getTitulo())
            );
    }

    public Traza crearTrazaTarjeta(Usuario autor, Tablero tablero, TarjetaId tarjetaId, TipoAccion accion) {
        String detalles = String.format("El usuario %s ha ejecutado %s en la tarjeta %s",
                            autor.getNombre(), accion, tarjetaId.valor());

        return new Traza(
            autor.getId(),
            tablero.getUrl(),
            tarjetaId,
            TipoObjeto.TARJETA,
            accion,
            detalles
        );
    }

    public Traza crearTrazaMovimiento(Usuario autor, Tablero tablero, TarjetaId tarjetaId, String origen, String destino) {
        String detalles = String.format("Movimiento de tarjeta %s: %s -> %s", tarjetaId.valor(), origen, destino);

        return new Traza(
            autor.getId(),
            tablero.getUrl(),
            tarjetaId,
            TipoObjeto.TARJETA,
            TipoAccion.MOVER,
            detalles
        );
    }

    public Traza crearTrazaLista(Usuario autor, Tablero tablero, String nombreLista, TipoAccion accion) {
        return new Traza(
            autor.getId(),
            tablero.getUrl(),
            TipoObjeto.LISTA,
            accion,
            "Lista afectada: " + nombreLista
        );
    }

    public Traza crearTrazaEtiqueta(Usuario autor, Tablero tablero, TarjetaId tarjetaId, String nombreEtiqueta, TipoAccion accion) {
        String accionTexto = (accion == TipoAccion.CREAR || accion == TipoAccion.MODIFICAR) ? "añadido" : "eliminado";
        String detalles = String.format("El usuario %s ha %s la etiqueta '%s' en la tarjeta %s",
                            autor.getNombre(), accionTexto, nombreEtiqueta, tarjetaId.valor());

        return new Traza(
            autor.getId(),
            tablero.getUrl(),
            tarjetaId,
            TipoObjeto.ETIQUETA,
            accion,
            detalles
        );
    }

    public Traza crearTrazaItemChecklist(Usuario autor, Tablero tablero, TarjetaId tarjetaId, String nombreItem, TipoAccion accion) {
        String estado = (accion == TipoAccion.COMPLETAR ? "completado" : "pendiente");
        String detalles = String.format("Ítem '%s' marcado como %s en la tarjeta %s por %s",
                            nombreItem, estado, tarjetaId.valor(), autor.getNombre());

        return new Traza(
            autor.getId(),
            tablero.getUrl(),
            tarjetaId,
            TipoObjeto.ITEM_CHECKLIST,
            accion,
            detalles
        );
    }
}