package com.sources.app.dao;

import com.sources.app.entities.BillMedicine;
import com.sources.app.entities.BillMedicineId;
import com.sources.app.entities.Bill;
import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class BillMedicineDAO {

    // CREATE
    public BillMedicine create(Bill bill, Medicine medicine, Integer quantity, Double cost, Double copay, String total) {
        Transaction tx = null;
        BillMedicine billMedicine = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            billMedicine = new BillMedicine();
            billMedicine.setBill(bill);
            billMedicine.setMedicine(medicine);
            billMedicine.setQuantity(quantity);
            billMedicine.setCost(cost);
            billMedicine.setCopay(copay);
            billMedicine.setTotal(total);

            // La clave compuesta se asigna automáticamente en setBill y setMedicine
            session.save(billMedicine);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return billMedicine;
    }

    // READ ALL
    public List<BillMedicine> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BillMedicine> query = session.createQuery("FROM BillMedicine", BillMedicine.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID (clave compuesta)
    public BillMedicine getById(BillMedicineId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BillMedicine.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public BillMedicine update(BillMedicine billMedicine) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(billMedicine);
            tx.commit();
            return billMedicine;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
