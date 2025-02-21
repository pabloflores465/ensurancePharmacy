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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return hospital;
    }

    public Hospital findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Hospital.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Hospital> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Hospital> query = session.createQuery("FROM Hospital", Hospital.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Hospital update(Hospital hospital) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(hospital);
            tx.commit();
            return hospital;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
