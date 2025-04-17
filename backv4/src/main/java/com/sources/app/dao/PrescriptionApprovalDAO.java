package com.sources.app.dao;

import com.sources.app.entities.PrescriptionApproval;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PrescriptionApprovalDAO {

    public PrescriptionApproval save(PrescriptionApproval approval) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(approval);
            tx.commit();
            return approval;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    public PrescriptionApproval findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PrescriptionApproval.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PrescriptionApproval> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PrescriptionApproval> query = session.createQuery(
                "FROM PrescriptionApproval ORDER BY approvalDate DESC", 
                PrescriptionApproval.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<PrescriptionApproval> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PrescriptionApproval> query = session.createQuery(
                "FROM PrescriptionApproval WHERE idUser = :userId ORDER BY approvalDate DESC", 
                PrescriptionApproval.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 