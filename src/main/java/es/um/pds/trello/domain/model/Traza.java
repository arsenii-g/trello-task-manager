package es.um.pds.trello.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "trazas")
public class Traza implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TrazaId id;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "usuario_email", nullable = false))
    private Email autor;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "tablero_url", nullable = false))
    private URL tableroId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "valor", column = @Column(name = "tarjeta_id_valor"))
    })
    private TarjetaId objetoAfectadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_objeto", nullable = false)
    private TipoObjeto tipoObjeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false)
    private TipoAccion accion;

    @Column(name = "detalles", length = 500)
    private String detalles;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fecha;

    protected Traza() {}

    public Traza(Email autor, URL tableroId, TipoObjeto tipo, TipoAccion accion, String detalles) {
        this(autor, tableroId, null, tipo, accion, detalles);
    }

    public Traza(Email autor, URL tableroId, TarjetaId objetoAfectadoId,
                 TipoObjeto tipo, TipoAccion accion, String detalles) {

        if (autor == null || tableroId == null || tipo == null || accion == null) {
            throw new IllegalArgumentException("Los datos básicos de la traza no pueden ser nulos.");
        }

        this.id = TrazaId.generar();
        this.autor = autor;
        this.tableroId = tableroId;
        this.objetoAfectadoId = objetoAfectadoId;
        this.tipoObjeto = tipo;
        this.accion = accion;
        this.detalles = detalles;
        this.fecha = LocalDateTime.now();
    }

    public TrazaId getId() { return id; }
    public Email getAutor() { return autor; }
    public URL getTableroId() { return tableroId; }
    public TarjetaId getObjetoAfectadoId() { return objetoAfectadoId; }
    public TipoObjeto getTipoObjeto() { return tipoObjeto; }
    public TipoAccion getAccion() { return accion; }
    public String getDetalles() { return detalles; }
    public LocalDateTime getFecha() { return fecha; }
}