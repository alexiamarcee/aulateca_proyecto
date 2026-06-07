package com.aulateca.dao;

import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

/** Operaciones CRUD genéricas con Hibernate. */
public abstract class GenericDAO<T, ID> {

    protected final Class<T> entityClass;

    protected GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void guardar(T entidad) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entidad);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public T actualizar(T entidad) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T resultado = em.merge(entidad);
            tx.commit();
            return resultado;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void eliminar(ID id) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entidad = em.find(entityClass, id);
            if (entidad != null) {
                em.remove(entidad);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<T> buscarPorId(ID id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            T entidad = em.find(entityClass, id);
            return Optional.ofNullable(entidad);
        } finally {
            em.close();
        }
    }

    public List<T> buscarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    public long contar() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
