package com.sources.app.dao;

import com.sources.app.entities.Service;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ServiceDAO {

    /**
     * Crea un nuevo Service usando la entidad completa con relaciones ya definidas.
     * Se espera que el objeto Service contenga instancias de Hospital, Category y Subcategory.
     */
    public Service create(Service service) {
        Transaction tx = null;
        Service createdService = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.save(service);
            tx.commit();
            createdService = service;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return createdService;
    }

    /**
     * Recupera un Service sin inicializar sus asociaciones.
     */
    public Service findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Service.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recupera un Service con sus asociaciones (hospital, category y subcategory) ya cargadas.
     */
    public Service findByIdWithDetails(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "select s from Service s " +
                    "join fetch s.hospital h " +
                    "join fetch s.category c " +
                    "join fetch s.subcategory sc " +
                    "where s.idService = :id";
            Query<Service> query = session.createQuery(hql, Service.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recupera la lista de Service sin inicializar las asociaciones.
     */
    public List<Service> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Service> query = session.createQuery("FROM Service", Service.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recupera la lista de Service con sus asociaciones (hospital, category y subcategory) ya cargadas.
     */
    public List<Service> findAllWithDetails() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "select s from Service s " +
                    "join fetch s.hospital h " +
                    "join fetch s.category c " +
                    "join fetch s.subcategory sc";
            Query<Service> query = session.createQuery(hql, Service.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza un Service. Se asume que el objeto Service ya tiene las asociaciones configuradas.
     */
    public Service update(Service service) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(service);
            tx.commit();
            return service;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
