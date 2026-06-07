package com.aulateca.service;

import com.aulateca.dao.*;
import com.aulateca.exception.BusinessException;
import com.aulateca.exception.ValidationException;
import com.aulateca.model.*;
import com.aulateca.service.dto.DisponibilidadCheckResult;

import java.time.LocalDate;
import java.util.List;

/** Lógica de negocio de reservas: validación, conflictos y disponibilidad. */
public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ResourceDAO    resourceDAO    = new ResourceDAO();
    private final UserDAO        userDAO        = new UserDAO();
    private final TimeSlotDAO    timeSlotDAO    = new TimeSlotDAO();

    /** Crea una reserva tras validar usuario, recurso, fecha y disponibilidad. */
    public Reservation crearReserva(User usuario, Resource recurso, LocalDate fecha,
                                     TimeSlot franja, String motivo) {
        validarUsuario(usuario);
        validarRecurso(recurso);
        validarFecha(fecha);
        validarFranja(franja);
        comprobarConflicto(recurso, fecha, franja, null);

        Reservation reserva = new Reservation(usuario, recurso, fecha, franja, motivo);
        try {
            reservationDAO.guardar(reserva);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo crear la reserva.", e);
        }
        return reserva;
    }

    /** Modifica una reserva existente aplicando las reglas de negocio. */
    public Reservation modificarReserva(Long id, User usuario, Resource recurso,
                                         LocalDate fecha, TimeSlot franja, String motivo) {
        Reservation reserva = reservationDAO.buscarPorId(id)
            .orElseThrow(() -> new ValidationException("Reserva no encontrada con ID: " + id));

        if (reserva.getEstado() == Reservation.Estado.CANCELADA) {
            throw new BusinessException("No se puede modificar una reserva cancelada.");
        }

        validarRecurso(recurso);
        validarFecha(fecha);
        validarFranja(franja);
        comprobarConflicto(recurso, fecha, franja, id);

        reserva.setUsuario(usuario);
        reserva.setRecurso(recurso);
        reserva.setFecha(fecha);
        reserva.setFranjaHoraria(franja);
        reserva.setMotivo(motivo);

        try {
            return reservationDAO.actualizar(reserva);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo actualizar la reserva.", e);
        }
    }

    /** Cancela una reserva confirmada. */
    public void cancelarReserva(Long id) {
        Reservation reserva = reservationDAO.buscarPorId(id)
            .orElseThrow(() -> new ValidationException("Reserva no encontrada con ID: " + id));

        if (reserva.getEstado() == Reservation.Estado.CANCELADA) {
            throw new BusinessException("La reserva ya está cancelada.");
        }

        reserva.setEstado(Reservation.Estado.CANCELADA);
        try {
            reservationDAO.actualizar(reserva);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo cancelar la reserva.", e);
        }
    }

    /**
     * Comprueba si un recurso está disponible en una fecha y franja concretas.
     * Devuelve un resultado estructurado para mostrar en la interfaz.
     */
    public DisponibilidadCheckResult verificarDisponibilidad(Resource recurso, LocalDate fecha,
                                                              TimeSlot franja, Long excludeId) {
        if (fecha == null) {
            return DisponibilidadCheckResult.aviso("Selecciona una fecha.");
        }
        if (recurso == null) {
            return DisponibilidadCheckResult.aviso("Selecciona un recurso.");
        }
        if (franja == null) {
            return DisponibilidadCheckResult.aviso("Selecciona una franja horaria.");
        }
        if (!recurso.getEstado().isReservable()) {
            return DisponibilidadCheckResult.error(
                "El recurso no está disponible (" + recurso.getEstado().getNombre() + ")");
        }
        if (reservationDAO.existeConflicto(recurso, fecha, franja, excludeId)) {
            return DisponibilidadCheckResult.error(
                "Ya existe una reserva para ese recurso, fecha y franja.");
        }
        return DisponibilidadCheckResult.ok("Disponible — puedes guardar.");
    }

    public List<TimeSlot> obtenerFranjasDisponibles(Resource recurso, LocalDate fecha,
                                                      List<TimeSlot> todasLasFranjas) {
        List<TimeSlot> ocupadas = reservationDAO.buscarFranjasOcupadas(recurso, fecha);
        return todasLasFranjas.stream()
            .filter(ts -> !ocupadas.contains(ts))
            .toList();
    }

    public List<Reservation> obtenerTodas() {
        return reservationDAO.buscarTodos();
    }

    public List<Reservation> obtenerPorUsuario(User usuario) {
        return reservationDAO.buscarPorUsuario(usuario);
    }

    public List<Reservation> obtenerPorRecurso(Resource recurso) {
        return reservationDAO.buscarPorRecurso(recurso);
    }

    public List<Reservation> obtenerPorFecha(LocalDate fecha) {
        return reservationDAO.buscarPorFecha(fecha);
    }

    public List<Reservation> obtenerEntreFechas(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        return reservationDAO.buscarEntreFechas(desde, hasta);
    }

    public List<Reservation> obtenerProximas() {
        return reservationDAO.buscarProximas();
    }

    /** Datos necesarios para el formulario de reserva. */
    public List<Resource> listarRecursos() {
        return resourceDAO.buscarTodos();
    }

    public List<User> listarUsuariosActivos() {
        return userDAO.buscarActivos();
    }

    public List<TimeSlot> listarFranjas() {
        return timeSlotDAO.buscarTodos();
    }

    private void validarUsuario(User usuario) {
        ValidacionUtil.requerirNoNulo(usuario, "Debe seleccionar un usuario para la reserva.");
        if (!usuario.isActivo()) {
            throw new ValidationException("El usuario no está activo en el sistema.");
        }
    }

    private void validarRecurso(Resource recurso) {
        ValidacionUtil.requerirNoNulo(recurso, "Debe seleccionar un recurso para la reserva.");
        if (!recurso.getEstado().isReservable()) {
            throw new ValidationException(
                "El recurso '" + recurso.getNombre() + "' no está disponible para reservar. " +
                "Estado actual: " + recurso.getEstado().getNombre());
        }
    }

    private void validarFecha(LocalDate fecha) {
        ValidacionUtil.requerirNoNulo(fecha, "Debe indicar una fecha para la reserva.");
        if (fecha.isBefore(LocalDate.now())) {
            throw new ValidationException("No se pueden crear reservas para fechas pasadas.");
        }
    }

    private void validarFranja(TimeSlot franja) {
        ValidacionUtil.requerirNoNulo(franja, "Debe seleccionar una franja horaria.");
    }

    private void comprobarConflicto(Resource recurso, LocalDate fecha,
                                     TimeSlot franja, Long excludeId) {
        if (reservationDAO.existeConflicto(recurso, fecha, franja, excludeId)) {
            throw new BusinessException(
                "Ya existe una reserva confirmada para '" + recurso.getNombre() +
                "' el " + fecha + " en la franja '" + franja.getNombre() + "'.");
        }
    }
}
