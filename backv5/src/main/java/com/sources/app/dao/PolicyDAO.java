package com.sources.app.dao;

import com.sources.app.entities.Policy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PolicyDAO {

    // CREATE
    public Policy create(Double percentage, Character enabled) {
        Transaction tx = null;
        Policy policy = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            policy = new Policy();
            policy.setPercentage(percentage);
            policy.setEnabled(enabled);

            session.save(policy);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return policy;
    }

    // READ ALL
    public List<Policy> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Policy> query = session.createQuery("FROM Policy", Policy.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Policy getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Policy.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
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
}
