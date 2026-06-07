package com.aulateca.controller;

import com.aulateca.service.AvailabilityService;
import com.aulateca.service.dto.DisponibilidadResultado;

import java.time.LocalDate;
import java.util.List;

/** Controlador de consulta de disponibilidad. */
public class AvailabilityController {

    private final AvailabilityService availabilityService = new AvailabilityService();

    public ControllerUtil.Resultado<DisponibilidadResultado> consultar(
            LocalDate fecha, String tipoFiltro) {
        return ControllerUtil.ejecutarConsulta(() ->
            availabilityService.consultar(fecha, tipoFiltro));
    }

    public ControllerUtil.Resultado<List<String>> nombresTiposRecurso() {
        return ControllerUtil.ejecutarConsulta(availabilityService::nombresTiposRecurso);
    }
}
