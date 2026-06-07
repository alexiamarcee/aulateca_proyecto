package com.aulateca.controller;

import com.aulateca.model.Reservation;
import com.aulateca.service.DashboardService;
import com.aulateca.service.dto.DashboardStats;

import java.util.List;

/** Controlador del panel de inicio. */
public class DashboardController {

    private final DashboardService dashboardService = new DashboardService();

    public ControllerUtil.Resultado<DashboardStats> obtenerEstadisticas() {
        return ControllerUtil.ejecutarConsulta(dashboardService::obtenerEstadisticas);
    }

    public ControllerUtil.Resultado<List<Reservation>> reservasDeHoy() {
        return ControllerUtil.ejecutarConsulta(dashboardService::reservasDeHoy);
    }
}
