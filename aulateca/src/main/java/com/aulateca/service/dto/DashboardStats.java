package com.aulateca.service.dto;

/** Estadísticas del panel de inicio. */
public record DashboardStats(
    long totalRecursos,
    long reservasHoy,
    long reservasEstaSemana,
    long usuariosActivos
) {}
