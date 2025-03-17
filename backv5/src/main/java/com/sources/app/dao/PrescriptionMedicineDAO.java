package com.sources.app.dao;

import com.sources.app.entities.PrescriptionMedicine;
import com.sources.app.entities.PrescriptionMedicineId;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PrescriptionMedicineDAO {

    // CREATE
    public PrescriptionMedicine create(Prescription prescription, Medicine medicine,
                                       Double dosis, Double frecuencia, Double duracion) {
        Transaction tx = null;
        PrescriptionMedicine pm = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            pm = new PrescriptionMedicine();
            pm.setPrescription(prescription);
            pm.setMedicine(medicine);
            pm.setDosis(dosis);
            pm.setFrecuencia(frecuencia);
            pm.setDuracion(duracion);

            session.save(pm);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return pm;
    }

    // READ ALL
    public List<PrescriptionMedicine> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PrescriptionMedicine> query = session.createQuery("FROM PrescriptionMedicine", PrescriptionMedicine.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID (clave compuesta)
    public PrescriptionMedicine getById(PrescriptionMedicineId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PrescriptionMedicine.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public PrescriptionMedicine update(PrescriptionMedicine pm) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(pm);
            tx.commit();
            return pm;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
