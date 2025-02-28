package com.sources.app.dao;

import com.sources.app.entities.Hospital;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class HospitalDAO {

    public Hospital create(String name, String address, Long phone, String email, Integer enabled) {
        Transaction tx = null;
        Hospital hospital = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            hospital = new Hospital();
            hospital.setName(name);
            hospital.setAddress(address);
            hospital.setPhone(phone);
            hospital.setEmail(email);
            hospital.setEnabled(enabled);

            session.save(hospital);
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
        return hospital;
    }

    public Hospital findById(Long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Hospital.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Hospital> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Hospital> query = session.createQuery("FROM Hospital", Hospital.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Hospital update(Hospital hospital) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(hospital);
            tx.commit();
            return hospital;
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
