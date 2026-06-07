package com.aulateca.dao;

import com.aulateca.model.ResourceStatus;
import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/** Acceso a datos de estados de recursos. */
public class ResourceStatusDAO extends GenericDAO<ResourceStatus, Long> {

    public ResourceStatusDAO() { super(ResourceStatus.class); }

    @Override
    public List<ResourceStatus> buscarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT rs FROM ResourceStatus rs ORDER BY rs.nombre", ResourceStatus.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<ResourceStatus> buscarReservables() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT rs FROM ResourceStatus rs WHERE rs.reservable = true ORDER BY rs.nombre",
                ResourceStatus.class).getResultList();
        } finally {
            em.close();
        }
    }
}
