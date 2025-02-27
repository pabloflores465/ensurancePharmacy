package com.sources.app.dao;

import com.sources.app.entities.ServiceCategory;
import com.sources.app.entities.ServiceCategoryId;
import com.sources.app.entities.Service;
import com.sources.app.entities.Category;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ServiceCategoryDAO {

    public ServiceCategory create(Long idService, Long idCategory) {
        Transaction tx = null;
        ServiceCategory serviceCategory = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar las entidades relacionadas
            Service service = session.get(Service.class, idService);
            Category category = session.get(Category.class, idCategory);

            if (service == null || category == null) {
                throw new RuntimeException("Service o Category no encontrado.");
            }

            serviceCategory = new ServiceCategory();
            serviceCategory.setService(service);
            serviceCategory.setCategory(category);

            session.save(serviceCategory);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return serviceCategory;
    }

    public ServiceCategory findById(Long idService, Long idCategory) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ServiceCategoryId scId = new ServiceCategoryId(idService, idCategory);
            return session.get(ServiceCategory.class, scId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ServiceCategory> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceCategory> query = session.createQuery("FROM ServiceCategory", ServiceCategory.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ServiceCategory update(ServiceCategory serviceCategory) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(serviceCategory);
            tx.commit();
            return serviceCategory;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
