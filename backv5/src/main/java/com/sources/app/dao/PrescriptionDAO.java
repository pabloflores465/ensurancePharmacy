package com.sources.app.dao;

import com.sources.app.entities.Prescription;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PrescriptionDAO {

    // CREATE
    public Prescription create(Hospital hospital, User user, Character approved) {
        Transaction tx = null;
        Prescription prescription = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            prescription = new Prescription();
            prescription.setHospital(hospital);
            prescription.setUser(user);
            prescription.setApproved(approved);

            session.save(prescription);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return prescription;
    }

    // READ ALL
    public List<Prescription> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery("FROM Prescription", Prescription.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Prescription getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Prescription.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Alias method for getById - to match method called in InsuranceIntegrationHandler
    public Prescription findById(Long id) {
        return getById(id);
    }

    // UPDATE
    public Prescription update(Prescription prescription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(prescription);
            tx.commit();
            return prescription;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
