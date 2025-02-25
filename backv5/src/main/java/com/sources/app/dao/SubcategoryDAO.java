package com.sources.app.dao;

import com.sources.app.entities.Subcategory;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class SubcategoryDAO {

    // CREATE
    public Subcategory create(String name) {
        Transaction tx = null;
        Subcategory subcategory = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            subcategory = new Subcategory();
            subcategory.setName(name);

            session.save(subcategory);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return subcategory;
    }

    // READ ALL
    public List<Subcategory> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Subcategory> query = session.createQuery("FROM Subcategory", Subcategory.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Subcategory getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Subcategory.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public Subcategory update(Subcategory subcategory) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(subcategory);
            tx.commit();
            return subcategory;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
