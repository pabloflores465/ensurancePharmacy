package com.sources.app.dao;

import com.sources.app.entities.ConfigurableAmount;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class ConfigurableAmountDAO {

    public ConfigurableAmount create(BigDecimal prescriptionAmount) {
        Transaction tx = null;
        ConfigurableAmount confAmount = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            confAmount = new ConfigurableAmount();
            confAmount.setPrescriptionAmount(prescriptionAmount);

            session.save(confAmount);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return confAmount;
    }

    public ConfigurableAmount findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ConfigurableAmount.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ConfigurableAmount> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ConfigurableAmount> query = session.createQuery("FROM ConfigurableAmount", ConfigurableAmount.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ConfigurableAmount update(ConfigurableAmount confAmount) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(confAmount);
            tx.commit();
            return confAmount;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
