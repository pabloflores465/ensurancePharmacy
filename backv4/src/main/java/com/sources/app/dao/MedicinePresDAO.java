package com.sources.app.dao;

import com.sources.app.entities.MedicinePres;
import com.sources.app.entities.MedicinePresId;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class MedicinePresDAO {

    public MedicinePres create(Long idPrescription, Long idMedicine) {
        Transaction tx = null;
        MedicinePres medPres = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar las entidades Prescription y Medicine
            Prescription prescription = session.get(Prescription.class, idPrescription);
            Medicine medicine = session.get(Medicine.class, idMedicine);
            if (prescription == null || medicine == null) {
                throw new RuntimeException("Prescription or Medicine not found");
            }

            medPres = new MedicinePres();
            medPres.setPrescription(prescription);
            medPres.setMedicine(medicine);

            session.save(medPres);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return medPres;
    }

    public MedicinePres findById(Long idPrescription, Long idMedicine) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            MedicinePresId key = new MedicinePresId(idPrescription, idMedicine);
            return session.get(MedicinePres.class, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MedicinePres> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MedicinePres> query = session.createQuery("FROM MedicinePres", MedicinePres.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MedicinePres update(MedicinePres medPres) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(medPres);
            tx.commit();
            return medPres;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
