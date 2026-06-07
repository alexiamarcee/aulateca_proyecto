package com.aulateca.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Franja horaria del centro. */
@Entity
@Table(name = "time_slots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    private int orden = 0;

    @OneToMany(mappedBy = "franjaHoraria", fetch = FetchType.LAZY)
    private List<Reservation> reservas = new ArrayList<>();

    public TimeSlot() {}

    public TimeSlot(String nombre, LocalTime horaInicio, LocalTime horaFin, int orden) {
        this.nombre = nombre;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.orden = orden;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public List<Reservation> getReservas() { return reservas; }
    public void setReservas(List<Reservation> reservas) { this.reservas = reservas; }

    public String getHorario() {
        return horaInicio + " - " + horaFin;
    }

    @Override
    public String toString() {
        return nombre + " (" + getHorario() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
