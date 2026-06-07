package com.aulateca.dao;

import com.aulateca.model.TimeSlot;
import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/** Acceso a datos de franjas horarias. */
public class TimeSlotDAO extends GenericDAO<TimeSlot, Long> {

    public TimeSlotDAO() { super(TimeSlot.class); }

    @Override
    public List<TimeSlot> buscarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT ts FROM TimeSlot ts ORDER BY ts.orden, ts.horaInicio", TimeSlot.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
