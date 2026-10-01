package es.um.pds.trello.domain.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "listas_tareas")
public class ListaTareas {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tablero_url", nullable = false)
    private Tablero tablero;

    private String titulo;
    private Integer posicion;

    @Column(name = "es_lista_completadas")
    private boolean esListaCompletadas;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "lista_id")
    private List<Tarjeta> tarjetas;

    @Column(name = "limite_tarjetas")
    private Integer limiteTarjetas;

    @ManyToMany
    @JoinTable(
        name = "lista_dependencias",
        joinColumns = @JoinColumn(name = "lista_destino_id"),
        inverseJoinColumns = @JoinColumn(name = "lista_predecesora_id")
    )
    private List<ListaTareas> predecesorasObligatorias = new ArrayList<>();

    protected ListaTareas() {}

    public ListaTareas(Tablero tablero, String titulo, Integer posicion, boolean esListaCompletadas, Integer limiteTarjetas) {
        this.id = UUID.randomUUID().toString();
        this.tablero = tablero;
        this.titulo = titulo;
        this.posicion = posicion;
        this.esListaCompletadas = esListaCompletadas;
        this.limiteTarjetas = limiteTarjetas;
        this.tarjetas = new ArrayList<>();
        this.predecesorasObligatorias = new ArrayList<>();
    }

    public void añadirTarjeta(Tarjeta tarjeta) {
        añadirTarjeta(tarjeta, true);
    }

    public void añadirTarjeta(Tarjeta tarjeta, boolean validar) {
        if (validar) {
            validarLimiteTarjetas();
            validarWorkflow(tarjeta);
        }
        if (this.esListaCompletadas && !tarjeta.isCompletada()) {
            throw new IllegalStateException("No se puede mover una tarjeta pendiente a la lista de completadas.");
        }
        tarjeta.setLista(this);
        tarjeta.registrarPasoPorLista(this.id);
        this.tarjetas.add(tarjeta);
    }

    public Tarjeta extraerTarjeta(TarjetaId tarjetaId) {
        Tarjeta encontrada = this.tarjetas.stream()
                .filter(t -> t.getId().equals(tarjetaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe en esta lista."));
        this.tarjetas.remove(encontrada);
        return encontrada;
    }

    public void configurarLimite(Integer nuevoLimite) {
        if (nuevoLimite != null && nuevoLimite < tarjetas.size()) {
            throw new IllegalArgumentException("El límite no puede ser inferior al número de tarjetas actuales.");
        }
        this.limiteTarjetas = nuevoLimite;
    }

    public void setPredecesoras(List<ListaTareas> nuevasPredecesoras) {
        if (nuevasPredecesoras == null) {
            this.predecesorasObligatorias = new ArrayList<>();
        } else {
            if (nuevasPredecesoras.stream().anyMatch(p -> p.getId().equals(this.id))) {
                throw new IllegalArgumentException("Una lista no puede depender de sí misma.");
            }
            this.predecesorasObligatorias = new ArrayList<>(nuevasPredecesoras);
        }
    }

    public void añadirPredecesora(ListaTareas predecesora) {
        if (predecesora.getId().equals(this.id)) {
            throw new IllegalArgumentException("Una lista no puede depender de sí misma.");
        }
        if (!this.predecesorasObligatorias.contains(predecesora)) {
            this.predecesorasObligatorias.add(predecesora);
        }
    }

    private void validarLimiteTarjetas() {
        if (limiteTarjetas != null && tarjetas.size() >= limiteTarjetas) {
            throw new IllegalStateException("Límite de tarjetas alcanzado en " + titulo);
        }
    }

    private void validarWorkflow(Tarjeta tarjeta) {
        for (ListaTareas pre : predecesorasObligatorias) {
            if (!tarjeta.haPasadoPorLista(pre.getId())) {
                throw new IllegalStateException("Debe pasar antes por la lista: " + pre.getTitulo());
            }
        }
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isEsListaCompletadas() { return esListaCompletadas; }
    public List<Tarjeta> getTarjetas() { return tarjetas; }
    public List<ListaTareas> getPredecesorasObligatorias() { return predecesorasObligatorias; }
    public Integer getLimiteTarjetas() {return limiteTarjetas;}
    public int getPosicion() {return posicion;}
}