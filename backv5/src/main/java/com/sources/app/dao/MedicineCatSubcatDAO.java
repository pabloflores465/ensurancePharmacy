package com.sources.app.dao;

import com.sources.app.entities.MedicineCatSubcat;
import com.sources.app.entities.MedicineCatSubcatId;
import com.sources.app.entities.Medicine;
import com.sources.app.entities.Category;
import com.sources.app.entities.Subcategory;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MedicineCatSubcatDAO {

    // CREATE
    public MedicineCatSubcat create(Medicine medicine, Category category, Subcategory subcategory) {
        Transaction tx = null;
        MedicineCatSubcat mcs = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            mcs = new MedicineCatSubcat();
            mcs.setMedicine(medicine);
            mcs.setCategory(category);
            mcs.setSubcategory(subcategory);

            session.save(mcs);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return mcs;
    }

    // READ ALL
    public List<MedicineCatSubcat> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MedicineCatSubcat> query = session.createQuery("FROM MedicineCatSubcat", MedicineCatSubcat.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID (clave compuesta)
    public MedicineCatSubcat getById(MedicineCatSubcatId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(MedicineCatSubcat.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public MedicineCatSubcat update(MedicineCatSubcat mcs) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(mcs);
            tx.commit();
            return mcs;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
