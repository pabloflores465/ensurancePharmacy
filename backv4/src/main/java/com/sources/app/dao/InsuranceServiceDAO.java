package com.sources.app.dao;

import com.sources.app.entities.InsuranceService;
import com.sources.app.entities.Category;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class InsuranceServiceDAO {

    // Crear un servicio de seguro
    public InsuranceService create(InsuranceService service) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            session.save(service);
            
            tx.commit();
            return service;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Obtener todos los servicios
    public List<InsuranceService> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<InsuranceService> query = session.createQuery("FROM InsuranceService", InsuranceService.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Obtener servicios por categoría
    public List<InsuranceService> findByCategory(Category category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<InsuranceService> query = session.createQuery(
                "FROM InsuranceService WHERE category = :category", 
                InsuranceService.class
            );
            query.setParameter("category", category);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Obtener servicios por subcategoría
    public List<InsuranceService> findBySubcategory(Category subcategory) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<InsuranceService> query = session.createQuery(
                "FROM InsuranceService WHERE subcategory = :subcategory", 
                InsuranceService.class
            );
            query.setParameter("subcategory", subcategory);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Obtener un servicio por ID
    public InsuranceService findById(Long idService) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(InsuranceService.class, idService);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Actualizar un servicio
    public InsuranceService update(InsuranceService service) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            session.update(service);
            
            tx.commit();
            return service;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Eliminar un servicio
    public boolean delete(Long idService) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            InsuranceService service = session.get(InsuranceService.class, idService);
            if (service != null) {
                session.delete(service);
                tx.commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Buscar por ID externo
    public List<InsuranceService> findByExternalId(String externalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<InsuranceService> query = session.createQuery(
                "FROM InsuranceService WHERE externalId = :externalId",
                InsuranceService.class
            );
            query.setParameter("externalId", externalId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 