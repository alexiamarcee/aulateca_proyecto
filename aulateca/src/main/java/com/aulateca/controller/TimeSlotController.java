package com.aulateca.controller;

import com.aulateca.model.TimeSlot;
import com.aulateca.service.TimeSlotService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/** Controlador de franjas horarias. */
public class TimeSlotController {

    private final TimeSlotService timeSlotService = new TimeSlotService();

    public ControllerUtil.Resultado<List<TimeSlot>> listarFranjas() {
        return ControllerUtil.ejecutarConsulta(timeSlotService::listarTodos);
    }

    public Optional<String> crear(String nombre, LocalTime horaInicio,
                                  LocalTime horaFin, int orden) {
        return ControllerUtil.ejecutar(() ->
            timeSlotService.crear(nombre, horaInicio, horaFin, orden));
    }

    public Optional<String> actualizar(TimeSlot slot, String nombre,
                                       LocalTime horaInicio, LocalTime horaFin, int orden) {
        return ControllerUtil.ejecutar(() ->
            timeSlotService.actualizar(slot, nombre, horaInicio, horaFin, orden));
    }

    public Optional<String> eliminar(Long id) {
        return ControllerUtil.ejecutar(() -> timeSlotService.eliminar(id));
    }

    public ControllerUtil.Resultado<LocalTime> parsearHora(String texto) {
        return ControllerUtil.ejecutarConsulta(() -> timeSlotService.parsearHora(texto));
    }
}
