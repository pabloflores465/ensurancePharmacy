package com.sources.app.dao;

import com.sources.app.entities.OrderMedicine;
import com.sources.app.entities.OrderMedicineId;
import com.sources.app.entities.Orders;
import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class OrderMedicineDAO {

    // CREATE
    public OrderMedicine create(Orders orders, Medicine medicine, Integer quantity, Double cost, String total) {
        Transaction tx = null;
        OrderMedicine om = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            om = new OrderMedicine();
            om.setOrders(orders);
            om.setMedicine(medicine);
            om.setQuantity(quantity);
            om.setCost(cost);
            om.setTotal(total);

            session.save(om);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return om;
    }

    // READ ALL
    public List<OrderMedicine> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<OrderMedicine> query = session.createQuery("FROM OrderMedicine", OrderMedicine.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID (clave compuesta)
    public OrderMedicine getById(OrderMedicineId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(OrderMedicine.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public OrderMedicine update(OrderMedicine om) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(om);
            tx.commit();
            return om;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
