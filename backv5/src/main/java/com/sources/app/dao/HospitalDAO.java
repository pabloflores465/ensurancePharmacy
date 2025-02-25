package com.sources.app.dao;

import com.sources.app.entities.Hospital;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class HospitalDAO {

    // CREATE
    public Hospital create(String name, String phone, String email, String address, Character enabled) {
        Transaction tx = null;
        Hospital hospital = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            hospital = new Hospital();
            hospital.setName(name);
            hospital.setPhone(phone);
            hospital.setEmail(email);
            hospital.setAddress(address);
            hospital.setEnabled(enabled);

            session.save(hospital);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return hospital;
    }

    // READ ALL
    public List<Hospital> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Hospital> query = session.createQuery("FROM Hospital", Hospital.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Hospital getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Hospital.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public Hospital update(Hospital hospital) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(hospital);
            tx.commit();
            return hospital;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
