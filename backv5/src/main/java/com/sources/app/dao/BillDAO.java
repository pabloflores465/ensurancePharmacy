package com.sources.app.dao;

import com.sources.app.entities.Bill;
import com.sources.app.entities.Prescription;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class BillDAO {

    // CREATE
    public Bill create(Prescription prescription, Double taxes, Double subtotal, Double copay, String total) {
        Transaction tx = null;
        Bill bill = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            bill = new Bill();
            bill.setPrescription(prescription);
            bill.setTaxes(taxes);
            bill.setSubtotal(subtotal);
            bill.setCopay(copay);
            bill.setTotal(total);

            session.save(bill);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return bill;
    }

    // READ ALL
    public List<Bill> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Bill> query = session.createQuery("FROM Bill", Bill.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Bill getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Bill.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public Bill update(Bill bill) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(bill);
            tx.commit();
            return bill;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
