package com.sources.app.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.sources.app.entities.User;

/**
 * Clase de utilidad para gestionar la SessionFactory de Hibernate.
 * Sigue el patrón Singleton para asegurar una única instancia de SessionFactory.
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Constructor privado para prevenir la instanciación de la clase de utilidad.
     */
    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure() // Carga hibernate.cfg.xml
                    .addAnnotatedClass(User.class) // Registra la entidad User
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Obtiene la instancia Singleton de SessionFactory.
     *
     * @return La SessionFactory configurada.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Configura los headers CORS para una petición
     * @param exchange el intercambio HTTP donde agregar los headers
     */
    public static void setCorsHeaders(com.sun.net.httpserver.HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
