package com.sources.app.dao;

import com.sources.app.entities.Medicine;
import com.sources.app.entities.Pharmacy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class MedicineDAO {

    public Medicine create(String name, String description, BigDecimal price, Pharmacy pharmacy,
                           Integer enabled, String activePrinciple, String presentation,
                           Integer stock, String brand, Integer coverage) {
        Transaction tx = null;
        Medicine medicine = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            medicine = new Medicine();
            medicine.setName(name);
            medicine.setDescription(description);
            medicine.setPrice(price);
            medicine.setPharmacy(pharmacy); // Se asigna el objeto Pharmacy
            medicine.setEnabled(enabled);
            medicine.setActivePrinciple(activePrinciple);
            medicine.setPresentation(presentation);
            medicine.setStock(stock);
            medicine.setBrand(brand);
            medicine.setCoverage(coverage); // Se asigna el atributo coverage

            session.save(medicine);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return medicine;
    }

    public Medicine findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Medicine.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Medicine> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medicine> query = session.createQuery("FROM Medicine", Medicine.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Medicine update(Medicine medicine) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(medicine);
            tx.commit();
            return medicine;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
