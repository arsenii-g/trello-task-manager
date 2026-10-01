package es.um.pds.trello.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjetas")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_tarjeta")
public abstract class Tarjeta {

    @EmbeddedId
    private TarjetaId id;

    private String titulo;
    private String descripcion;
    private boolean completada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_id")
    private ListaTareas lista;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarjeta_etiquetas", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private List<Etiqueta> etiquetas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "tarjeta_historial_listas",
        joinColumns = @JoinColumn(name = "tarjeta_id_valor")
    )
    @Column(name = "lista_id")
    private Set<String> historialListas = new HashSet<>();

    protected Tarjeta() {}

    protected Tarjeta(String titulo, String descripcion) {
        this.id = TarjetaId.generar();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
        this.historialListas = new HashSet<>();
    }

    public void registrarPasoPorLista(String listaId) {
        if (listaId != null) {
            this.historialListas.add(listaId);
        }
    }

    public boolean haPasadoPorLista(String listaId) {
        return this.historialListas.contains(listaId);
    }

    public void marcarComoCompletada() {
        this.completada = true;
    }

    public void marcarComoPendiente() {
        this.completada = false;
    }

    public void asignarEtiqueta(Etiqueta etiqueta) {
        if (!this.etiquetas.contains(etiqueta)) {
            this.etiquetas.add(etiqueta);
        }
    }

    public void eliminarEtiquetaPorNombre(String nombreEtiqueta) {
        this.etiquetas.removeIf(e -> e.nombre().equalsIgnoreCase(nombreEtiqueta));
    }

    public void setLista(ListaTareas lista) {
        this.lista = lista;
    }

    public ListaTareas getLista() {
        return lista;
    }

    public TarjetaId getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public boolean isCompletada() { return completada; }

    public List<Etiqueta> getEtiquetas() {
        return this.etiquetas;
    }

    public Set<String> getHistorialListas() {
        return Collections.unmodifiableSet(historialListas);
    }

    public abstract boolean puedeCompletarse();

	public void actualizarDatos(String titulo, String descripcion) {
		this.titulo = titulo;
		this.descripcion = descripcion;
	}
}