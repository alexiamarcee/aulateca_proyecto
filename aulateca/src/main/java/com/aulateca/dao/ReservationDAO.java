package com.aulateca.dao;

import com.aulateca.model.*;
import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

/** Acceso a datos de reservas. */
public class ReservationDAO extends GenericDAO<Reservation, Long> {

    public ReservationDAO() { super(Reservation.class); }

    @Override
    public List<Reservation> buscarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reservation r " +
                "JOIN FETCH r.usuario JOIN FETCH r.recurso JOIN FETCH r.franjaHoraria " +
                "ORDER BY r.fecha DESC, r.franjaHoraria.orden",
                Reservation.class).getResultList();
        } finally {
            em.close();
        }
    }

    /** Evita reservas duplicadas para el mismo recurso, fecha y franja. */
    public boolean existeConflicto(Resource recurso, LocalDate fecha,
                                    TimeSlot franja, Long excludeId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM Reservation r " +
                "WHERE r.recurso = :recurso AND r.fecha = :fecha " +
                "AND r.franjaHoraria = :franja AND r.estado = :estado" +
                (excludeId != null ? " AND r.id <> :excludeId" : "");

            var query = em.createQuery(jpql, Long.class)
                .setParameter("recurso", recurso)
                .setParameter("fecha", fecha)
                .setParameter("franja", franja)
                .setParameter("estado", Reservation.Estado.CONFIRMADA);

            if (excludeId != null) {
                query.setParameter("excludeId", excludeId);
            }

            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public List<TimeSlot> buscarFranjasOcupadas(Resource recurso, LocalDate fecha) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r.franjaHoraria FROM Reservation r " +
                "WHERE r.recurso = :recurso AND r.fecha = :fecha AND r.estado = :estado " +
                "ORDER BY r.franjaHoraria.orden",
                TimeSlot.class)
                .setParameter("recurso", recurso)
                .setParameter("fecha", fecha)
                .setParameter("estado", Reservation.Estado.CONFIRMADA)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> buscarPorUsuario(User usuario) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reservation r JOIN FETCH r.recurso JOIN FETCH r.franjaHoraria " +
                "WHERE r.usuario = :usuario ORDER BY r.fecha DESC",
                Reservation.class)
                .setParameter("usuario", usuario)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> buscarPorRecurso(Resource recurso) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reservation r JOIN FETCH r.usuario JOIN FETCH r.franjaHoraria " +
                "WHERE r.recurso = :recurso ORDER BY r.fecha DESC",
                Reservation.class)
                .setParameter("recurso", recurso)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> buscarPorFecha(LocalDate fecha) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reservation r " +
                "JOIN FETCH r.usuario JOIN FETCH r.recurso JOIN FETCH r.franjaHoraria " +
                "WHERE r.fecha = :fecha AND r.estado = :estado " +
                "ORDER BY r.franjaHoraria.orden, r.recurso.nombre",
                Reservation.class)
                .setParameter("fecha", fecha)
                .setParameter("estado", Reservation.Estado.CONFIRMADA)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> buscarEntreFechas(LocalDate desde, LocalDate hasta) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reservation r " +
                "JOIN FETCH r.usuario JOIN FETCH r.recurso JOIN FETCH r.franjaHoraria " +
                "WHERE r.fecha BETWEEN :desde AND :hasta AND r.estado = :estado " +
                "ORDER BY r.fecha, r.franjaHoraria.orden",
                Reservation.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .setParameter("estado", Reservation.Estado.CONFIRMADA)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> buscarProximas() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reservation r " +
                "JOIN FETCH r.usuario JOIN FETCH r.recurso JOIN FETCH r.franjaHoraria " +
                "WHERE r.fecha >= :hoy AND r.estado = :estado " +
                "ORDER BY r.fecha, r.franjaHoraria.orden",
                Reservation.class)
                .setParameter("hoy", LocalDate.now())
                .setParameter("estado", Reservation.Estado.CONFIRMADA)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
