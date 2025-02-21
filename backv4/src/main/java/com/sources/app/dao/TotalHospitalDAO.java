package com.sources.app.dao;

import com.sources.app.entities.TotalHospital;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TotalHospitalDAO {

    public TotalHospital create(Long idHospital, Date totalDate, BigDecimal total) {
        Transaction tx = null;
        TotalHospital th = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            th = new TotalHospital();
            th.setIdHospital(idHospital);
            th.setTotalDate(totalDate);
            th.setTotal(total);

            session.save(th);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return th;
    }

    public TotalHospital findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TotalHospital.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TotalHospital> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<TotalHospital> query = session.createQuery("FROM TotalHospital", TotalHospital.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TotalHospital update(TotalHospital th) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(th);
            tx.commit();
            return th;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
