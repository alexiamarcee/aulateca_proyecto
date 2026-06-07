package com.aulateca.service;

import com.aulateca.dao.ReservationDAO;
import com.aulateca.dao.ResourceDAO;
import com.aulateca.dao.UserDAO;
import com.aulateca.model.Reservation;
import com.aulateca.service.dto.DashboardStats;

import java.time.LocalDate;
import java.util.List;

/** Lógica de negocio del panel de inicio. */
public class DashboardService {

    private final ResourceDAO    resourceDAO    = new ResourceDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final UserDAO        userDAO        = new UserDAO();

    /** Obtiene las estadísticas principales del dashboard. */
    public DashboardStats obtenerEstadisticas() {
        return new DashboardStats(
            resourceDAO.contar(),
            reservationDAO.buscarPorFecha(LocalDate.now()).size(),
            reservationDAO.buscarEstaSemana().size(),
            userDAO.buscarActivos().size()
        );
    }

    /** Devuelve las reservas confirmadas del día actual. */
    public List<Reservation> reservasDeHoy() {
        return reservationDAO.buscarPorFecha(LocalDate.now());
    }
}
