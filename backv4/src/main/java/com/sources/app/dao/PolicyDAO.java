package com.sources.app.dao;

import com.sources.app.entities.Policy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.Date;
import java.util.List;

public class PolicyDAO {

    // Método para crear una nueva Policy
    public Policy create(Float percentage, Date creation_date, Date exp_date, Float cost, Integer enabled) {
        Transaction tx = null;
        Policy policy = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Creamos una instancia de Policy y asignamos los valores
            policy = new Policy();
            policy.setPercentage(percentage);
            policy.setCreationDate(creation_date);
            policy.setExpDate(exp_date);
            policy.setCost(cost);
            policy.setEnabled(enabled);

            // Guardamos la policy en la base de datos
            session.save(policy);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // Verificamos si la transacción está activa antes de hacer rollback
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        }
        return policy;
    }


    // Método para obtener una Policy por su ID
    public Policy find(Long idPolicy) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Policy.class, idPolicy);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para obtener todas las Policies
    public List<Policy> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Policy> query = session.createQuery("FROM Policy", Policy.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para actualizar una Policy existente
    public Policy update(Policy policy) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(policy);
            tx.commit();
            return policy;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Método para eliminar una Policy por su ID
    public boolean delete(Long idPolicy) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Policy policy = session.get(Policy.class, idPolicy);
            if (policy != null) {
                session.delete(policy);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
}
