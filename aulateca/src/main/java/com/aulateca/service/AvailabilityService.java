package com.aulateca.service;

import com.aulateca.dao.ReservationDAO;
import com.aulateca.dao.ResourceDAO;
import com.aulateca.dao.ResourceTypeDAO;
import com.aulateca.dao.TimeSlotDAO;
import com.aulateca.exception.ValidationException;
import com.aulateca.model.Resource;
import com.aulateca.model.TimeSlot;
import com.aulateca.service.dto.DisponibilidadResultado;
import com.aulateca.service.dto.DisponibilidadResultado.EstadoCelda;
import com.aulateca.service.dto.DisponibilidadResultado.FilaDisponibilidad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Consulta de disponibilidad de recursos por fecha y franja. */
public class AvailabilityService {

    private final ResourceDAO     resourceDAO     = new ResourceDAO();
    private final TimeSlotDAO     timeSlotDAO     = new TimeSlotDAO();
    private final ReservationDAO  reservationDAO  = new ReservationDAO();
    private final ResourceTypeDAO resourceTypeDAO = new ResourceTypeDAO();

    /** Consulta la disponibilidad aplicando filtro opcional por tipo de recurso. */
    public DisponibilidadResultado consultar(LocalDate fecha, String tipoFiltro) {
        ValidacionUtil.requerirNoNulo(fecha, "Selecciona una fecha.");

        List<TimeSlot> franjas  = timeSlotDAO.buscarTodos();
        List<Resource> recursos = resourceDAO.buscarTodos();

        if (tipoFiltro != null && !"Todos los tipos".equals(tipoFiltro)) {
            recursos = recursos.stream()
                .filter(r -> r.getTipo().getNombre().equals(tipoFiltro))
                .toList();
        }

        String fechaFormateada = formatearFecha(fecha);
        List<String> nombresFranjas = franjas.stream().map(TimeSlot::getNombre).toList();
        List<FilaDisponibilidad> filas = new ArrayList<>();

        for (Resource recurso : recursos) {
            List<TimeSlot> ocupadas = reservationDAO.buscarFranjasOcupadas(recurso, fecha);
            List<EstadoCelda> celdas = new ArrayList<>();
            for (TimeSlot franja : franjas) {
                if (!recurso.getEstado().isReservable()) {
                    celdas.add(EstadoCelda.BLOQUEADO);
                } else if (ocupadas.contains(franja)) {
                    celdas.add(EstadoCelda.OCUPADO);
                } else {
                    celdas.add(EstadoCelda.LIBRE);
                }
            }
            filas.add(new FilaDisponibilidad(recurso.getNombre(), celdas));
        }

        boolean sinDatos = recursos.isEmpty() || franjas.isEmpty();
        return new DisponibilidadResultado(
            fecha, fechaFormateada, nombresFranjas, filas, sinDatos);
    }

    /** Devuelve los nombres de tipos para el filtro del panel. */
    public List<String> nombresTiposRecurso() {
        List<String> nombres = new ArrayList<>();
        nombres.add("Todos los tipos");
        resourceTypeDAO.buscarTodos().forEach(t -> nombres.add(t.getNombre()));
        return nombres;
    }

    private String formatearFecha(LocalDate fecha) {
        String texto = fecha.format(DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
