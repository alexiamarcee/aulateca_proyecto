package com.aulateca.controller;

import com.aulateca.model.*;
import com.aulateca.service.ReservationService;
import com.aulateca.service.dto.DisponibilidadCheckResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Controlador de gestión de reservas. */
public class ReservationController {

    private final ReservationService reservationService = new ReservationService();

    /** Lista reservas según el índice del filtro de la vista. */
    public ControllerUtil.Resultado<List<Reservation>> listarReservas(int filtroIndice) {
        return ControllerUtil.ejecutarConsulta(() -> switch (filtroIndice) {
            case 1  -> reservationService.obtenerProximas();
            case 2  -> reservationService.obtenerPorFecha(LocalDate.now());
            case 3  -> {
                LocalDate ini = LocalDate.now().withDayOfMonth(1);
                yield reservationService.obtenerEntreFechas(ini, ini.plusMonths(1).minusDays(1));
            }
            default -> reservationService.obtenerTodas();
        });
    }

    public Optional<String> cancelarReserva(Long id) {
        return ControllerUtil.ejecutar(() -> reservationService.cancelarReserva(id));
    }

    public Optional<String> crearReserva(User usuario, Resource recurso, LocalDate fecha,
                                           TimeSlot franja, String motivo) {
        return ControllerUtil.ejecutar(() ->
            reservationService.crearReserva(usuario, recurso, fecha, franja, motivo));
    }

    public Optional<String> modificarReserva(Long id, User usuario, Resource recurso,
                                              LocalDate fecha, TimeSlot franja, String motivo) {
        return ControllerUtil.ejecutar(() ->
            reservationService.modificarReserva(id, usuario, recurso, fecha, franja, motivo));
    }

    public DisponibilidadCheckResult verificarDisponibilidad(Resource recurso, LocalDate fecha,
                                                              TimeSlot franja, Long excludeId) {
        return reservationService.verificarDisponibilidad(recurso, fecha, franja, excludeId);
    }

    public ControllerUtil.Resultado<List<Resource>> listarRecursos() {
        return ControllerUtil.ejecutarConsulta(reservationService::listarRecursos);
    }

    public ControllerUtil.Resultado<List<User>> listarUsuariosActivos() {
        return ControllerUtil.ejecutarConsulta(reservationService::listarUsuariosActivos);
    }

    public ControllerUtil.Resultado<List<TimeSlot>> listarFranjas() {
        return ControllerUtil.ejecutarConsulta(reservationService::listarFranjas);
    }
}
