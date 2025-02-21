package com.sources.app.dao;

import com.sources.app.entities.Service;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ServiceDAO {

    public Service create(Long idHospital, String name, String description,
                          Long idCategory, Long idSubcategory, Double cost,
                          Integer enabled) {
        Transaction tx = null;
        Service service = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            service = new Service();
            service.setIdHospital(idHospital);
            service.setName(name);
            service.setDescription(description);
            service.setIdCategory(idCategory);
            service.setIdSubcategory(idSubcategory);
            service.setCost(cost);
            service.setEnabled(enabled);

            session.save(service);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return service;
    }

    public Service findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Service.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Service> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Service> query = session.createQuery("FROM Service", Service.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
