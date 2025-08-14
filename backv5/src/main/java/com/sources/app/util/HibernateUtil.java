package com.sources.app.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sources.app.entities.User;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration()
                    .configure(); // Carga hibernate.cfg.xml

            // Configurar esquema desde variable de entorno
            String dbSchema = System.getenv("DB_SCHEMA_PHARMACY");
            if (dbSchema != null && !dbSchema.trim().isEmpty()) {
                System.out.println("🔧 Configurando esquema de BD para farmacia: " + dbSchema);
                configuration.setProperty("hibernate.default_schema", dbSchema);
            } else {
                System.out.println("⚠️ Variable DB_SCHEMA_PHARMACY no definida, usando esquema por defecto");
            }

            return configuration
                    .addAnnotatedClass(User.class) // Registra la entidad User
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
