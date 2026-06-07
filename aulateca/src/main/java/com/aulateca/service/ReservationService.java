package com.aulateca.service;

import com.aulateca.dao.*;
import com.aulateca.model.*;
import java.time.LocalDate;
import java.util.List;

/** Lógica de negocio de reservas. */
public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();

    public Reservation crearReserva(User usuario, Resource recurso, LocalDate fecha,
                                     TimeSlot franja, String motivo) {
        validarUsuario(usuario);
        validarRecurso(recurso);
        validarFecha(fecha);
        validarFranja(franja);
        comprobarConflicto(recurso, fecha, franja, null);

        Reservation reserva = new Reservation(usuario, recurso, fecha, franja, motivo);
        reservationDAO.guardar(reserva);
        return reserva;
    }

    public Reservation modificarReserva(Long id, User usuario, Resource recurso,
                                         LocalDate fecha, TimeSlot franja, String motivo) {
        Reservation reserva = reservationDAO.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));

        if (reserva.getEstado() == Reservation.Estado.CANCELADA) {
            throw new IllegalArgumentException("No se puede modificar una reserva cancelada.");
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

        return reservationDAO.actualizar(reserva);
    }

    public void cancelarReserva(Long id) {
        Reservation reserva = reservationDAO.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));

        if (reserva.getEstado() == Reservation.Estado.CANCELADA) {
            throw new IllegalArgumentException("La reserva ya está cancelada.");
        }

        reserva.setEstado(Reservation.Estado.CANCELADA);
        reservationDAO.actualizar(reserva);
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
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        return reservationDAO.buscarEntreFechas(desde, hasta);
    }

    public List<Reservation> obtenerProximas() {
        return reservationDAO.buscarProximas();
    }

    private void validarUsuario(User usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Debe seleccionar un usuario para la reserva.");
        }
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no está activo en el sistema.");
        }
    }

    private void validarRecurso(Resource recurso) {
        if (recurso == null) {
            throw new IllegalArgumentException("Debe seleccionar un recurso para la reserva.");
        }
        if (!recurso.getEstado().isReservable()) {
            throw new IllegalArgumentException(
                "El recurso '" + recurso.getNombre() + "' no está disponible para reservar. " +
                "Estado actual: " + recurso.getEstado().getNombre());
        }
    }

    private void validarFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe indicar una fecha para la reserva.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden crear reservas para fechas pasadas.");
        }
    }

    private void validarFranja(TimeSlot franja) {
        if (franja == null) {
            throw new IllegalArgumentException("Debe seleccionar una franja horaria.");
        }
    }

    private void comprobarConflicto(Resource recurso, LocalDate fecha,
                                     TimeSlot franja, Long excludeId) {
        if (reservationDAO.existeConflicto(recurso, fecha, franja, excludeId)) {
            throw new IllegalArgumentException(
                "Ya existe una reserva confirmada para '" + recurso.getNombre() +
                "' el " + fecha + " en la franja '" + franja.getNombre() + "'.");
        }
    }
}
