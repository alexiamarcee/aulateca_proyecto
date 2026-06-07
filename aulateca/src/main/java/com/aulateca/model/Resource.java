package com.aulateca.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Recurso del centro (aula, proyector, etc.). */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tipo_id", nullable = false)
    private ResourceType tipo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private ResourceStatus estado;

    @Column(length = 100)
    private String ubicacion;

    @OneToMany(mappedBy = "recurso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservas = new ArrayList<>();

    public Resource() {}

    public Resource(String nombre, String descripcion, ResourceType tipo,
                    ResourceStatus estado, String ubicacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public ResourceType getTipo() { return tipo; }
    public void setTipo(ResourceType tipo) { this.tipo = tipo; }

    public ResourceStatus getEstado() { return estado; }
    public void setEstado(ResourceStatus estado) { this.estado = estado; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public List<Reservation> getReservas() { return reservas; }
    public void setReservas(List<Reservation> reservas) { this.reservas = reservas; }

    @Override
    public String toString() { return nombre; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
