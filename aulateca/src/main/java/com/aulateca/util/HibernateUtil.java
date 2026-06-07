package com.aulateca.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/** Gestión del EntityManagerFactory de Hibernate. */
public class HibernateUtil {

    private static final String PERSISTENCE_UNIT = "aulatecaPU";
    private static EntityManagerFactory emf;

    private HibernateUtil() {}

    public static synchronized void init() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            init();
        }
        return emf.createEntityManager();
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
