package com.sources.app.dao;

import com.sources.app.entities.SystemConfig;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class SystemConfigDAO {

    // Crear o actualizar una configuración del sistema
    public SystemConfig saveOrUpdate(String configKey, String configValue, String description) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Verificar si ya existe la configuración
            SystemConfig config = getByKey(configKey);
            
            if (config == null) {
                // Crear nueva configuración
                config = new SystemConfig();
                config.setConfigKey(configKey);
                config.setConfigValue(configValue);
                config.setDescription(description);
                session.save(config);
            } else {
                // Actualizar configuración existente
                config.setConfigValue(configValue);
                if (description != null && !description.isEmpty()) {
                    config.setDescription(description);
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
    
    // Obtener una configuración por su clave
    public SystemConfig getByKey(String configKey) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SystemConfig> query = session.createQuery(
                "FROM SystemConfig WHERE configKey = :key", SystemConfig.class);
            query.setParameter("key", configKey);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Obtener todas las configuraciones del sistema
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
    
    // Eliminar una configuración
    public boolean delete(Long idConfig) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            SystemConfig config = session.get(SystemConfig.class, idConfig);
            if (config != null) {
                session.delete(config);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    // Obtener un valor de configuración con un valor predeterminado
    public String getConfigValue(String configKey, String defaultValue) {
        SystemConfig config = getByKey(configKey);
        return (config != null) ? config.getConfigValue() : defaultValue;
    }
    
    // Obtener un valor de configuración numérico con un valor predeterminado
    public Double getConfigValueAsDouble(String configKey, Double defaultValue) {
        SystemConfig config = getByKey(configKey);
        if (config != null) {
            try {
                return Double.parseDouble(config.getConfigValue());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    // Inicializar configuraciones predeterminadas del sistema
    public void initializeDefaultConfigs() {
        // Configurar el monto mínimo para aprobación de recetas
        if (getByKey("MIN_PRESCRIPTION_AMOUNT") == null) {
            saveOrUpdate("MIN_PRESCRIPTION_AMOUNT", "250.00", 
                "Monto mínimo en quetzales para aprobar una receta");
        }
    }
} 