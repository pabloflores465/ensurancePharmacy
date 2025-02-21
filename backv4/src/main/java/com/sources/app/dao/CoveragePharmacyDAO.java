package com.sources.app.dao;

import com.sources.app.entities.CoveragePharmacy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CoveragePharmacyDAO {

    public CoveragePharmacy create(Long idPharmacy, Long idMedicine, Integer coverage) {
        Transaction tx = null;
        CoveragePharmacy covPharmacy = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            covPharmacy = new CoveragePharmacy();
            covPharmacy.setIdPharmacy(idPharmacy);
            covPharmacy.setIdMedicine(idMedicine);
            covPharmacy.setCoverage(coverage);

            session.save(covPharmacy);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return covPharmacy;
    }

    public CoveragePharmacy findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(CoveragePharmacy.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CoveragePharmacy> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CoveragePharmacy> query = session.createQuery("FROM CoveragePharmacy", CoveragePharmacy.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CoveragePharmacy update(CoveragePharmacy covPharmacy) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(covPharmacy);
            tx.commit();
            return covPharmacy;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
