package es.um.pds.trello.domain.model;

import java.util.ArrayList;
import jakarta.persistence.*;
import java.util.List;
@Entity
@DiscriminatorValue("CHECKLIST")
public class Checklist extends Tarjeta {

    @ElementCollection
    @CollectionTable(
        name = "checklist_items",
        joinColumns = @JoinColumn(name = "checklist_id")
    )
    private List<ItemChecklist> items;

    protected Checklist() {
        super();
    }

    public Checklist(String titulo, String descripcion) {
        super(titulo, descripcion);
        this.items = new ArrayList<>();
    }

    public void añadirItem(String contenido) {
        this.items.add(new ItemChecklist(contenido, false));

        if (this.isCompletada()) {
            this.marcarComoPendiente();
        }
    }

    public void cambiarEstadoItem(int indice, boolean nuevoEstado) {
        if (indice < 0 || indice >= items.size()) {
            throw new IllegalArgumentException("Índice de ítem no válido");
        }

        ItemChecklist itemActual = items.get(indice);
        items.set(indice, new ItemChecklist(itemActual.descripcion(), nuevoEstado));

        this.actualizarEstadoSegunItems();
    }

    private void actualizarEstadoSegunItems() {
        if (items.isEmpty()) {
            this.marcarComoPendiente();
            return;
        }

        boolean todosCompletos = items.stream().allMatch(ItemChecklist::estado);

        if (todosCompletos) {
            this.marcarComoCompletada();
        } else {
            this.marcarComoPendiente();
        }
    }

    @Override
    public boolean puedeCompletarse() {
        return !items.isEmpty() && items.stream().allMatch(ItemChecklist::estado);
    }

    @Override
    public void marcarComoCompletada() {
        if (!puedeCompletarse()) {

            return;
        }
        super.marcarComoCompletada();
    }

    public List<ItemChecklist> getItems() {
        return new ArrayList<>(items);
    }

    public void eliminarItem(int indice) {
        if (indice >= 0 && indice < items.size()) {
            this.items.remove(indice);
            this.actualizarEstadoSegunItems();
        }
    }

    public String getNombreItem(int indiceItem) {
        if (indiceItem < 0 || indiceItem >= this.items.size()) {
            throw new IllegalArgumentException("Índice de ítem inválido: " + indiceItem);
        }
        return this.items.get(indiceItem).descripcion();
    }
}