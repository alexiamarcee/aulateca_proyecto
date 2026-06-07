package com.aulateca.service;

import com.aulateca.dao.TimeSlotDAO;
import com.aulateca.exception.BusinessException;
import com.aulateca.exception.ValidationException;
import com.aulateca.model.TimeSlot;

import java.time.LocalTime;
import java.util.List;

/** Lógica de negocio de franjas horarias. */
public class TimeSlotService {

    private final TimeSlotDAO timeSlotDAO = new TimeSlotDAO();

    public List<TimeSlot> listarTodos() {
        return timeSlotDAO.buscarTodos();
    }

    public void crear(String nombre, LocalTime horaInicio, LocalTime horaFin, int orden) {
        validarFranja(nombre, horaInicio, horaFin);
        try {
            timeSlotDAO.guardar(new TimeSlot(nombre.trim(), horaInicio, horaFin, orden));
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo crear la franja.", e);
        }
    }

    public void actualizar(TimeSlot slot, String nombre,
                           LocalTime horaInicio, LocalTime horaFin, int orden) {
        ValidacionUtil.requerirNoNulo(slot, "Franja no encontrada.");
        validarFranja(nombre, horaInicio, horaFin);

        slot.setNombre(nombre.trim());
        slot.setHoraInicio(horaInicio);
        slot.setHoraFin(horaFin);
        slot.setOrden(orden);

        try {
            timeSlotDAO.actualizar(slot);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo actualizar la franja.", e);
        }
    }

    public void eliminar(Long id) {
        ValidacionUtil.requerirNoNulo(id, "Identificador no válido.");
        try {
            timeSlotDAO.eliminar(id);
        } catch (RuntimeException e) {
            throw new BusinessException("No se puede eliminar: " + e.getMessage(), e);
        }
    }

    /** Parsea una hora en formato HH:mm. */
    public LocalTime parsearHora(String texto) {
        ValidacionUtil.requerirNoVacio(texto, "La hora es obligatoria.");
        try {
            return LocalTime.parse(texto.trim());
        } catch (Exception e) {
            throw new ValidationException("Formato de hora incorrecto. Usa HH:mm (ej: 08:30).");
        }
    }

    private void validarFranja(String nombre, LocalTime horaInicio, LocalTime horaFin) {
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");
        ValidacionUtil.requerirNoNulo(horaInicio, "La hora de inicio es obligatoria.");
        ValidacionUtil.requerirNoNulo(horaFin, "La hora de fin es obligatoria.");
        if (!horaFin.isAfter(horaInicio)) {
            throw new ValidationException("La hora de fin debe ser posterior a la hora de inicio.");
        }
    }
}
