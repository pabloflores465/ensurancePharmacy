package com.sources.app.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sources.app.entities.User;

/**
 * Clase de utilidad para gestionar la SessionFactory de Hibernate. Sigue el
 * patrón Singleton para asegurar una única instancia de SessionFactory.
 */
public class HibernateUtil {

    // Lazy-initialized to avoid heavy static initialization during tests
    private static volatile SessionFactory sessionFactory;

    /**
     * Constructor privado para prevenir la instanciación de la clase de
     * utilidad.
     */
    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration()
                .configure(); // Carga hibernate.cfg.xml

        // Configurar esquema desde variable de entorno
        String dbSchema = System.getenv("DB_SCHEMA_ENSURANCE");
        if (dbSchema != null && !dbSchema.trim().isEmpty()) {
            System.out.println("🔧 Configurando esquema de BD para seguros: " + dbSchema);
            configuration.setProperty("hibernate.default_schema", dbSchema);
        } else {
            System.out.println("⚠️ Variable DB_SCHEMA_ENSURANCE no definida, usando esquema por defecto");
        }

        return configuration
                .addAnnotatedClass(User.class) // Registra la entidad User
                .buildSessionFactory();
    }

    /**
     * Obtiene la instancia Singleton de SessionFactory.
     *
     * @return La SessionFactory configurada.
     */
    public static SessionFactory getSessionFactory() {
        SessionFactory localRef = sessionFactory;
        if (localRef == null) {
            synchronized (HibernateUtil.class) {
                localRef = sessionFactory;
                if (localRef == null) {
                    sessionFactory = localRef = buildSessionFactory();
                }
            }
        }
        return localRef;
    }

    /**
     * Permite inyectar una SessionFactory (principalmente para tests).
     */
    public static void setSessionFactory(SessionFactory injectedSessionFactory) {
        synchronized (HibernateUtil.class) {
            sessionFactory = injectedSessionFactory;
        }
    }

    /**
     * Configura los headers CORS para una petición
     *
     * @param exchange el intercambio HTTP donde agregar los headers
     */
    public static void setCorsHeaders(com.sun.net.httpserver.HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
