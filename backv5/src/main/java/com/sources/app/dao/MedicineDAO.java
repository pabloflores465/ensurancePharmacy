package com.sources.app.dao;

import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MedicineDAO {

    // CREATE
    public Medicine create(String name, String activeMedicament, String description, String image,
                           String concentration, Double presentacion, Integer stock, String brand,
                           Boolean prescription, Double price, Integer soldUnits) {
        Transaction tx = null;
        Medicine med = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            med = new Medicine();
            med.setName(name);
            med.setActiveMedicament(activeMedicament);
            med.setDescription(description);
            med.setImage(image);
            med.setConcentration(concentration);
            med.setPresentacion(presentacion);
            med.setStock(stock);
            med.setBrand(brand);
            med.setPrescription(prescription);
            med.setPrice(price);
            med.setSoldUnits(soldUnits);

            session.save(med);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return med;
    }

    // READ ALL
    public List<Medicine> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medicine> query = session.createQuery("FROM Medicine", Medicine.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Medicine getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Medicine.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public Medicine update(Medicine medicine) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(medicine);
            tx.commit();
            return medicine;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
