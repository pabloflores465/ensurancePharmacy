package com.sources.app.dao;

import com.sources.app.entities.MedicinePres;
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

            medPres = new MedicinePres();
            medPres.setIdPrescription(idPrescription);
            medPres.setIdMedicine(idMedicine);

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

    public MedicinePres findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(MedicinePres.class, id);
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
