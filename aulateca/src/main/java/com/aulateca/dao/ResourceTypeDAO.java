package com.aulateca.dao;

import com.aulateca.model.ResourceType;
import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/** Acceso a datos de tipos de recursos. */
public class ResourceTypeDAO extends GenericDAO<ResourceType, Long> {

    public ResourceTypeDAO() { super(ResourceType.class); }

    @Override
    public List<ResourceType> buscarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT rt FROM ResourceType rt ORDER BY rt.nombre", ResourceType.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
