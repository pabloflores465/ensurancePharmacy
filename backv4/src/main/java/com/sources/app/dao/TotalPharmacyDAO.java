package com.sources.app.dao;

import com.sources.app.entities.TotalPharmacy;
import com.sources.app.entities.Pharmacy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TotalPharmacyDAO {

    public TotalPharmacy create(Long idPharmacy, Date totalDate, BigDecimal total) {
        Transaction tx = null;
        TotalPharmacy tp = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar la entidad Pharmacy a partir de su id
            Pharmacy pharmacy = session.get(Pharmacy.class, idPharmacy);
            if (pharmacy == null) {
                throw new RuntimeException("Pharmacy no encontrada para id: " + idPharmacy);
            }

            tp = new TotalPharmacy();
            tp.setPharmacy(pharmacy);  // Asignar la entidad Pharmacy
            tp.setTotalDate(totalDate);
            tp.setTotal(total);

            session.save(tp);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return tp;
    }

    public TotalPharmacy findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TotalPharmacy.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TotalPharmacy> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<TotalPharmacy> query = session.createQuery("FROM TotalPharmacy", TotalPharmacy.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TotalPharmacy update(TotalPharmacy tp) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(tp);
            tx.commit();
            return tp;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
