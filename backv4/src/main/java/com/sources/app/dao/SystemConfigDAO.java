package com.sources.app.dao;

import com.sources.app.entities.SystemConfig;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class SystemConfigDAO {

    /**
     * Guarda o actualiza una configuración
     */
    public SystemConfig saveOrUpdate(String key, String value, String description) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Buscar si ya existe la configuración
            Query<SystemConfig> query = session.createQuery(
                "FROM SystemConfig WHERE configKey = :key", SystemConfig.class);
            query.setParameter("key", key);
            SystemConfig config = query.uniqueResult();
            
            if (config == null) {
                // Crear nueva configuración
                config = new SystemConfig(key, value, description);
                session.save(config);
            } else {
                // Actualizar configuración existente
                config.setConfigValue(value);
                if (description != null) {
                    config.setConfigDescription(description);
                }
                session.update(config);
            }
            
            transaction.commit();
            return config;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene una configuración por su clave
     */
    public SystemConfig getByKey(String key) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SystemConfig> query = session.createQuery(
                "FROM SystemConfig WHERE configKey = :key", SystemConfig.class);
            query.setParameter("key", key);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene el valor de una configuración como String
     */
    public String getConfigValue(String key, String defaultValue) {
        SystemConfig config = getByKey(key);
        return config != null ? config.getConfigValue() : defaultValue;
    }
    
    /**
     * Obtiene el valor de una configuración como Integer
     */
    public Integer getConfigValueAsInt(String key, Integer defaultValue) {
        String value = getConfigValue(key, null);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtiene el valor de una configuración como Double
     */
    public Double getConfigValueAsDouble(String key, Double defaultValue) {
        String value = getConfigValue(key, null);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtiene el valor de una configuración como Boolean
     */
    public Boolean getConfigValueAsBoolean(String key, Boolean defaultValue) {
        String value = getConfigValue(key, null);
        if (value == null) {
            return defaultValue;
        }
        
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }
    
    /**
     * Elimina una configuración
     */
    public boolean delete(String key) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Query<?> query = session.createQuery("DELETE FROM SystemConfig WHERE configKey = :key");
            query.setParameter("key", key);
            int result = query.executeUpdate();
            
            transaction.commit();
            return result > 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene todas las configuraciones
     */
    public List<SystemConfig> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SystemConfig> query = session.createQuery(
                "FROM SystemConfig ORDER BY configKey", SystemConfig.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Inicializa las configuraciones por defecto si no existen
     */
    public void initDefaultConfigs() {
        // Monto mínimo para aprobación de recetas
        if (getByKey("MIN_PRESCRIPTION_AMOUNT") == null) {
            saveOrUpdate("MIN_PRESCRIPTION_AMOUNT", "250.00", 
                "Monto mínimo para la aprobación de recetas en quetzales");
        }
        
        // Otras configuraciones por defecto que se necesiten
    }
} 