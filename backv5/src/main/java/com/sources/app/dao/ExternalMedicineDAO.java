package com.sources.app.dao;

import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ExternalMedicineDAO {
    
    public List<Medicine> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medicine> query = session.createQuery("FROM Medicine", Medicine.class);
            return query.list();
        }
    }
}
