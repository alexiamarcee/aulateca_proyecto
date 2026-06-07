package com.aulateca.service.dto;

import java.time.LocalDate;
import java.util.List;

/** Resultado de la consulta de disponibilidad por recurso y franja. */
public record DisponibilidadResultado(
    LocalDate fecha,
    String fechaFormateada,
    List<String> nombresFranjas,
    List<FilaDisponibilidad> filas,
    boolean sinDatos
) {

    public enum EstadoCelda { LIBRE, OCUPADO, BLOQUEADO }

    public record FilaDisponibilidad(String nombreRecurso, List<EstadoCelda> celdas) {}
}
