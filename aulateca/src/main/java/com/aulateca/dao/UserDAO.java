package com.aulateca.dao;

import com.aulateca.model.User;
import com.aulateca.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de usuarios. */
public class UserDAO extends GenericDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> buscarPorEmail(String email) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<User> resultado = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
            return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<User> autenticar(String email, String password) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<User> resultado = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email AND u.password = :pass AND u.activo = true",
                User.class)
                .setParameter("email", email)
                .setParameter("pass", password)
                .getResultList();
            return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
        } finally {
            em.close();
        }
    }

    public List<User> buscarActivos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM User u WHERE u.activo = true ORDER BY u.apellidos, u.nombre",
                User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<User> buscarPorRol(User.Rol rol) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM User u WHERE u.rol = :rol AND u.activo = true ORDER BY u.apellidos",
                User.class)
                .setParameter("rol", rol)
                .getResultList();
        } finally {
            em.close();
        }
    }
}
