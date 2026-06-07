package com.aulateca.dao;

import com.aulateca.model.Resource;
import com.aulateca.model.ResourceStatus;
import com.aulateca.model.ResourceType;
import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/** Acceso a datos de recursos. */
public class ResourceDAO extends GenericDAO<Resource, Long> {

    public ResourceDAO() {
        super(Resource.class);
    }

    @Override
    public List<Resource> buscarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Resource r JOIN FETCH r.tipo JOIN FETCH r.estado ORDER BY r.nombre",
                Resource.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Resource> buscarPorTipo(ResourceType tipo) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Resource r WHERE r.tipo = :tipo ORDER BY r.nombre",
                Resource.class)
                .setParameter("tipo", tipo)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Resource> buscarReservables() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Resource r WHERE r.estado.reservable = true ORDER BY r.nombre",
                Resource.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Resource> buscarPorEstado(ResourceStatus estado) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Resource r WHERE r.estado = :estado ORDER BY r.nombre",
                Resource.class)
                .setParameter("estado", estado)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
