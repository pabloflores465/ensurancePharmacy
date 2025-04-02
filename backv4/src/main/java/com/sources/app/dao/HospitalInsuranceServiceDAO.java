package com.sources.app.dao;

import com.sources.app.entities.HospitalInsuranceService;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.InsuranceService;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Date;

public class HospitalInsuranceServiceDAO {

    // Aprobar un servicio para un hospital
    public HospitalInsuranceService approveService(Hospital hospital, InsuranceService service, String notes) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            // Verificar si ya existe la relación
            Query<HospitalInsuranceService> query = session.createQuery(
                "FROM HospitalInsuranceService WHERE hospital = :hospital AND insuranceService = :service",
                HospitalInsuranceService.class
            );
            query.setParameter("hospital", hospital);
            query.setParameter("service", service);
            
            HospitalInsuranceService relation = query.uniqueResult();
            
            if (relation == null) {
                // Si no existe, crear una nueva
                relation = new HospitalInsuranceService();
                relation.setHospital(hospital);
                relation.setInsuranceService(service);
            }
            
            // Actualizar estado y datos
            relation.setApproved(1);
            relation.setApprovalDate(new Date());
            relation.setNotes(notes);
            
            session.saveOrUpdate(relation);
            
            tx.commit();
            return relation;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // Revocar aprobación de un servicio para un hospital
    public boolean revokeApproval(Hospital hospital, InsuranceService service) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            // Buscar la relación
            Query<HospitalInsuranceService> query = session.createQuery(
                "FROM HospitalInsuranceService WHERE hospital = :hospital AND insuranceService = :service",
                HospitalInsuranceService.class
            );
            query.setParameter("hospital", hospital);
            query.setParameter("service", service);
            
            HospitalInsuranceService relation = query.uniqueResult();
            
            if (relation != null) {
                relation.setApproved(0);
                session.update(relation);
                tx.commit();
                return true;
            }
            
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // Obtener servicios aprobados para un hospital
    public List<HospitalInsuranceService> findApprovedByHospital(Hospital hospital) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HospitalInsuranceService> query = session.createQuery(
                "FROM HospitalInsuranceService WHERE hospital = :hospital AND approved = 1",
                HospitalInsuranceService.class
            );
            query.setParameter("hospital", hospital);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Obtener hospitales que ofrecen un servicio específico
    public List<HospitalInsuranceService> findHospitalsByService(InsuranceService service) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HospitalInsuranceService> query = session.createQuery(
                "FROM HospitalInsuranceService WHERE insuranceService = :service AND approved = 1",
                HospitalInsuranceService.class
            );
            query.setParameter("service", service);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Buscar por ID
    public HospitalInsuranceService findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(HospitalInsuranceService.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Eliminar relación
    public boolean delete(Long id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            HospitalInsuranceService relation = session.get(HospitalInsuranceService.class, id);
            if (relation != null) {
                session.delete(relation);
                tx.commit();
                return true;
            }
            
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
} 