package com.sources.app.dao;

import com.sources.app.entities.Orders;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class OrdersDAO {

    // CREATE
    public Orders create(String status) {
        Transaction tx = null;
        Orders order = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            order = new Orders();
            order.setStatus(status);

            session.save(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return order;
    }

    // READ ALL
    public List<Orders> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Orders> query = session.createQuery("FROM Orders", Orders.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Orders getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Orders.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public Orders update(Orders order) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(order);
            tx.commit();
            return order;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
