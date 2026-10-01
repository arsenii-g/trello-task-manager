package es.um.pds.trello.domain.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tableros")
public class Tablero {

    @EmbeddedId
    private URL url;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "dueño_email", nullable = false))
    private Email dueñoId;

    private String titulo;

    @Embedded
    private Estado estado;

    @OneToMany(mappedBy = "tablero", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("posicion ASC")
    private List<ListaTareas> listas;

    protected Tablero() {}

    public Tablero(String titulo, Email dueñoId) {
        if (dueñoId == null) throw new IllegalArgumentException("R3: El dueño debe tener un email");
        this.titulo = titulo;
        this.dueñoId = dueñoId;
        this.url = URL.generar();
        this.estado = Estado.activo();
        this.listas = new ArrayList<>();
    }

    public void añadirLista(String titulo, Boolean esCompletada, Integer limite) {
        validarSiEstaBloqueado();
        int nuevaPosicion = listas.size();
        ListaTareas nuevaLista = new ListaTareas(this, titulo, nuevaPosicion, esCompletada, limite);
        this.listas.add(nuevaLista);
    }

    public void añadirListaCompletadas(String titulo) {
        validarSiEstaBloqueado();
        int nuevaPosicion = listas.size();
        this.listas.add(new ListaTareas(this, titulo, nuevaPosicion, true, null));
    }

    public String eliminarLista(String idLista) {
        validarSiEstaBloqueado();
        ListaTareas listaAEliminar = buscarListaPorId(idLista);
        String nombreLista = listaAEliminar.getTitulo();
        this.listas.remove(listaAEliminar);
        return nombreLista;
    }

    public void configurarLimiteLista(String listaId, Integer nuevoLimite) {
        validarSiEstaBloqueado();
        buscarListaPorId(listaId).configurarLimite(nuevoLimite);
    }

    public void actualizarWorkflowDeLista(String listaId, List<String> nuevosPredecesoresIds) {
        validarSiEstaBloqueado();
        ListaTareas destino = buscarListaPorId(listaId);

        List<ListaTareas> predecesorasObjetos = this.listas.stream()
                .filter(l -> nuevosPredecesoresIds.contains(l.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        destino.setPredecesoras(predecesorasObjetos);
    }

    public void establecerDependenciaDeLista(String listaDestinoId, String listaPredecesoraId) {
        validarSiEstaBloqueado();
        ListaTareas destino = buscarListaPorId(listaDestinoId);
        ListaTareas predecesora = buscarListaPorId(listaPredecesoraId);
        destino.añadirPredecesora(predecesora);
    }

    public void añadirTarjetaALista(String listaId, Tarjeta tarjeta) {
        validarSiEstaBloqueado();
        buscarListaPorId(listaId).añadirTarjeta(tarjeta);
    }

    public void moverTarjeta(TarjetaId tarjetaId, String idListaOrigen, String idListaDestino) {
        ListaTareas origen = buscarListaPorId(idListaOrigen);
        ListaTareas destino = buscarListaPorId(idListaDestino);

        Tarjeta tarjeta = origen.extraerTarjeta(tarjetaId);

        try {
            destino.añadirTarjeta(tarjeta, true);
        } catch (IllegalStateException e) {
            origen.añadirTarjeta(tarjeta, false);
            throw e;
        }
    }

    public void eliminarTarjeta(TarjetaId tarjetaId) {
        validarSiEstaBloqueado();
        encontrarListaDeTarjeta(tarjetaId).extraerTarjeta(tarjetaId);
    }

    public void cambiarEstadoTarjeta(TarjetaId tarjetaId, boolean completada) {
        Tarjeta tarjeta = buscarTarjetaEnTablero(tarjetaId);
        ListaTareas listaActual = encontrarListaDeTarjeta(tarjetaId);

        if (completada) {
            tarjeta.marcarComoCompletada();
            if (!listaActual.isEsListaCompletadas()) {
                moverAListaCompletadas(tarjetaId, listaActual.getId());
            }
        } else {
            tarjeta.marcarComoPendiente();
        }
    }

    public void añadirItemAChecklist(TarjetaId tarjetaId, String contenido) {
        Checklist checklist = buscarChecklist(tarjetaId);
        ListaTareas listaActual = encontrarListaDeTarjeta(tarjetaId);

        boolean estabaCompletada = checklist.isCompletada();
        checklist.añadirItem(contenido);

        if (estabaCompletada && !checklist.isCompletada() && listaActual.isEsListaCompletadas()) {
            ListaTareas destino = listas.stream()
                    .filter(l -> !l.isEsListaCompletadas())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No hay listas para devolver la tarjeta"));
            this.moverTarjeta(tarjetaId, listaActual.getId(), destino.getId());
        }
    }

    public void cambiarEstadoItemChecklist(TarjetaId tarjetaId, int indice, boolean nuevoEstado) {
        Checklist checklist = buscarChecklist(tarjetaId);
        ListaTareas listaActual = encontrarListaDeTarjeta(tarjetaId);

        checklist.cambiarEstadoItem(indice, nuevoEstado);

        if (checklist.isCompletada() && !listaActual.isEsListaCompletadas()) {
            moverAListaCompletadas(tarjetaId, listaActual.getId());
        }
    }

    public void eliminarItemDeChecklist(TarjetaId tarjetaId, int indice) {
        validarSiEstaBloqueado();
        buscarChecklist(tarjetaId).eliminarItem(indice);
    }

    public String obtenerNombreItem(TarjetaId tarjetaId, int indiceItem) {
        return buscarChecklist(tarjetaId).getNombreItem(indiceItem);
    }

    public void añadirEtiquetaATarjeta(TarjetaId tarjetaId, Etiqueta etiqueta) {
        validarSiEstaBloqueado();
        buscarTarjetaEnTablero(tarjetaId).asignarEtiqueta(etiqueta);
    }

    public void eliminarEtiquetaDeTarjeta(TarjetaId tarjetaId, String nombreEtiqueta) {
        validarSiEstaBloqueado();
        buscarTarjetaEnTablero(tarjetaId).eliminarEtiquetaPorNombre(nombreEtiqueta);
    }

    private void validarSiEstaBloqueado() {
        if (this.estado.esBloqueado()) throw new IllegalStateException("R1: Tablero bloqueado.");
    }

    private void moverAListaCompletadas(TarjetaId tarjetaId, String origenId) {
        ListaTareas destino = listas.stream()
                .filter(ListaTareas::isEsListaCompletadas)
                .findFirst()
                .orElseGet(() -> {
                    int nuevaPosicion = listas.size();
                    ListaTareas nuevaLista = new ListaTareas(this, "COMPLETADAS", nuevaPosicion, true, null);
                    this.listas.add(nuevaLista);
                    return nuevaLista;
                });
        this.moverTarjeta(tarjetaId, origenId, destino.getId());
    }

    private ListaTareas buscarListaPorId(String id) {
        return listas.stream().filter(l -> l.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lista no encontrada: " + id));
    }

    private Tarjeta buscarTarjetaEnTablero(TarjetaId id) {
        return listas.stream().flatMap(l -> l.getTarjetas().stream())
                .filter(t -> t.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));
    }

    private ListaTareas encontrarListaDeTarjeta(TarjetaId id) {
        return listas.stream().filter(l -> l.getTarjetas().stream().anyMatch(t -> t.getId().equals(id)))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Tarjeta no asociada a lista"));
    }

    private Checklist buscarChecklist(TarjetaId id) {
        Tarjeta t = buscarTarjetaEnTablero(id);
        if (t instanceof Checklist c) return c;
        throw new IllegalArgumentException("La tarjeta no es de tipo Checklist");
    }

    public void bloquearTablero() { this.estado = Estado.bloqueado(); }
    public void desbloquearTablero() { this.estado = Estado.activo(); }

    public URL getUrl() { return url; }
    public String getTitulo() { return titulo; }
    public Email getDueñoId() { return dueñoId; }
    public List<ListaTareas> getListas() { return Collections.unmodifiableList(listas); }

	public Estado getEstado() {
		return estado;
	}

	public void modificarDatosTarjeta(TarjetaId id, String titulo, String descripcion) {
		validarSiEstaBloqueado();
		buscarTarjetaEnTablero(id).actualizarDatos(titulo, descripcion);
	}
}