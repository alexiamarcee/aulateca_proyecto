package com.aulateca.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Reserva de un recurso en una fecha y franja. */
@Entity
@Table(
    name = "reservations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_recurso_fecha_franja",
            columnNames = {"recurso_id", "fecha", "franja_horaria_id"}
        )
    }
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Resource recurso;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "franja_horaria_id", nullable = false)
    private TimeSlot franjaHoraria;

    @Column(length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.CONFIRMADA;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum Estado {
        CONFIRMADA, CANCELADA, PENDIENTE
    }

    public Reservation() {}

    public Reservation(User usuario, Resource recurso, LocalDate fecha,
                       TimeSlot franjaHoraria, String motivo) {
        this.usuario = usuario;
        this.recurso = recurso;
        this.fecha = fecha;
        this.franjaHoraria = franjaHoraria;
        this.motivo = motivo;
        this.estado = Estado.CONFIRMADA;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Resource getRecurso() { return recurso; }
    public void setRecurso(Resource recurso) { this.recurso = recurso; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public TimeSlot getFranjaHoraria() { return franjaHoraria; }
    public void setFranjaHoraria(TimeSlot franjaHoraria) { this.franjaHoraria = franjaHoraria; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    @Override
    public String toString() {
        return String.format("Reserva - %s | %s | %s | %s",
            recurso.getNombre(), fecha, franjaHoraria.getNombre(), estado);
    }
}
