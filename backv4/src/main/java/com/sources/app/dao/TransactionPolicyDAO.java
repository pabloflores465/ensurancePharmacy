package com.sources.app.dao;

import com.sources.app.entities.TransactionPolicy;
import com.sources.app.entities.Policy;
import com.sources.app.entities.User;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TransactionPolicyDAO {

    public TransactionPolicy create(Long idPolicy, Long idUser, Date payDate, BigDecimal total) {
        Transaction tx = null;
        TransactionPolicy tp = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar las entidades relacionadas
            Policy policy = session.get(Policy.class, idPolicy);
            User user = session.get(User.class, idUser);
            if (policy == null || user == null) {
                throw new RuntimeException("Policy o User no encontrados.");
            }

            tp = new TransactionPolicy();
            tp.setPolicy(policy); // Asignar la entidad Policy
            tp.setUser(user);     // Asignar la entidad User
            tp.setPayDate(payDate);
            tp.setTotal(total);

            session.save(tp);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return tp;
    }

    public TransactionPolicy findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TransactionPolicy.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TransactionPolicy> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<TransactionPolicy> query = session.createQuery("FROM TransactionPolicy", TransactionPolicy.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TransactionPolicy update(TransactionPolicy tp) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(tp);
            tx.commit();
            return tp;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
