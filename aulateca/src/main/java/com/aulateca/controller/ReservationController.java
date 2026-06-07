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

    /** Lista reservas según el filtro; los alumnos solo ven las propias. */
    public ControllerUtil.Resultado<List<Reservation>> listarReservas(User operador, int filtroIndice) {
        return ControllerUtil.ejecutarConsulta(() -> {
            List<Reservation> lista = switch (filtroIndice) {
                case 1  -> reservationService.obtenerEstaSemana();
                case 2  -> reservationService.obtenerPorFecha(LocalDate.now());
                case 3  -> {
                    LocalDate ini = LocalDate.now().withDayOfMonth(1);
                    yield reservationService.obtenerEntreFechas(ini, ini.plusMonths(1).minusDays(1));
                }
                default -> reservationService.obtenerTodas();
            };
            if (!operador.puedeGestionarReservasAjena()) {
                return lista.stream()
                    .filter(r -> r.getUsuario().getId().equals(operador.getId()))
                    .toList();
            }
            return lista;
        });
    }

    public Optional<String> cancelarReserva(User operador, Long id) {
        return ControllerUtil.ejecutar(() -> reservationService.cancelarReserva(operador, id));
    }

    public Optional<String> crearReserva(User operador, User usuario, Resource recurso,
                                           LocalDate fecha, TimeSlot franja, String motivo) {
        return ControllerUtil.ejecutar(() ->
            reservationService.crearReserva(operador, usuario, recurso, fecha, franja, motivo));
    }

    public Optional<String> modificarReserva(User operador, Long id, User usuario,
                                              Resource recurso, LocalDate fecha,
                                              TimeSlot franja, String motivo) {
        return ControllerUtil.ejecutar(() ->
            reservationService.modificarReserva(operador, id, usuario, recurso, fecha, franja, motivo));
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
