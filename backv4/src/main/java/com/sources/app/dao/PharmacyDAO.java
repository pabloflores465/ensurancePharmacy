package com.sources.app.dao;

import com.sources.app.entities.Pharmacy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PharmacyDAO {

    public Pharmacy create(String name, String address, Long phone, String email, Integer enabled) {
        Transaction tx = null;
        Pharmacy pharmacy = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            pharmacy = new Pharmacy();
            pharmacy.setName(name);
            pharmacy.setAddress(address);
            pharmacy.setPhone(phone);
            pharmacy.setEmail(email);
            pharmacy.setEnabled(enabled);

            session.save(pharmacy);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    rbEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return pharmacy;
    }

    public Pharmacy findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Pharmacy.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Pharmacy> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Pharmacy> query = session.createQuery("FROM Pharmacy", Pharmacy.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Pharmacy update(Pharmacy pharmacy) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(pharmacy);
            tx.commit();
            return pharmacy;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    rbEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
