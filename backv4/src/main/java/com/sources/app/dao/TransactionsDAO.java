package com.sources.app.dao;

import com.sources.app.entities.Transactions;
import com.sources.app.entities.User;
import com.sources.app.entities.Hospital;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class TransactionsDAO {

    public Transactions create(Long idUser, Long idHospital, Date transDate, Double total,
                               Double copay, String transactionComment, String result,
                               Integer covered, String auth) {
        Transaction tx = null;
        Transactions t = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar las entidades relacionadas a partir de sus IDs
            User user = session.get(User.class, idUser);
            Hospital hospital = session.get(Hospital.class, idHospital);
            if (user == null || hospital == null) {
                throw new RuntimeException("User or Hospital not found.");
            }

            t = new Transactions();
            t.setUser(user);
            t.setHospital(hospital);
            t.setTransDate(transDate);
            t.setTotal(total);
            t.setCopay(copay);
            t.setTransactionComment(transactionComment);
            t.setResult(result);
            t.setCovered(covered);
            t.setAuth(auth);

            session.save(t);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return t;
    }

    public Transactions findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Transactions.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transactions> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transactions> query = session.createQuery("FROM Transactions", Transactions.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Transactions update(Transactions t) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(t);
            tx.commit();
            return t;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
