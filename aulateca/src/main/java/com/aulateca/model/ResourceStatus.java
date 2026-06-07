package com.aulateca.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Estado operativo de un recurso. */
@Entity
@Table(name = "resource_status")
public class ResourceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private boolean reservable = true;

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    private List<Resource> recursos = new ArrayList<>();

    public ResourceStatus() {}

    public ResourceStatus(String nombre, String descripcion, boolean reservable) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.reservable = reservable;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isReservable() { return reservable; }
    public void setReservable(boolean reservable) { this.reservable = reservable; }

    public List<Resource> getRecursos() { return recursos; }
    public void setRecursos(List<Resource> recursos) { this.recursos = recursos; }

    @Override
    public String toString() { return nombre; }
}
