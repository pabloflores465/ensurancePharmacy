package com.sources.app.dao;

import com.sources.app.entities.ServiceApproval;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * DAO para las operaciones de aprobaciones de servicios médicos
 */
public class ServiceApprovalDAO {

    /**
     * Crea una nueva aprobación de servicio
     */
    public ServiceApproval create(ServiceApproval approval) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Generar código de aprobación único si no existe
            if (approval.getApprovalCode() == null || approval.getApprovalCode().isEmpty()) {
                String approvalCode = generateApprovalCode();
                approval.setApprovalCode(approvalCode);
            }
            
            // Establecer fecha de aprobación si no existe
            if (approval.getApprovalDate() == null) {
                approval.setApprovalDate(new Date());
            }
            
            session.save(approval);
            transaction.commit();
            return approval;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Actualiza una aprobación de servicio existente y devuelve el objeto actualizado
     */
    public ServiceApproval update(ServiceApproval approval) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(approval);
            transaction.commit();
            return approval;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Actualiza la información de receta en una aprobación existente
     */
    public ServiceApproval updatePrescriptionInfo(String approvalCode, Long prescriptionId, Double prescriptionTotal) {
        try {
            ServiceApproval approval = getByApprovalCode(approvalCode);
            if (approval == null) {
                return null;
            }
            
            approval.setPrescriptionId(prescriptionId);
            approval.setPrescriptionTotal(prescriptionTotal);
            
            return update(approval);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Actualiza el estado de una aprobación
     */
    public ServiceApproval updateStatus(String approvalCode, String newStatus, String rejectionReason) {
        try {
            ServiceApproval approval = getByApprovalCode(approvalCode);
            if (approval == null) {
                return null;
            }
            
            approval.setStatus(newStatus);
            
            if (newStatus.equals("REJECTED") && rejectionReason != null) {
                approval.setRejectionReason(rejectionReason);
            }
            
            if (newStatus.equals("COMPLETED")) {
                approval.setCompletedDate(new Date());
            }
            
            return update(approval);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene una aprobación por su ID
     */
    public ServiceApproval getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ServiceApproval.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene una aprobación por su código
     */
    public ServiceApproval getByApprovalCode(String approvalCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval WHERE approvalCode = :code", ServiceApproval.class);
            query.setParameter("code", approvalCode);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las aprobaciones de un usuario
     */
    public List<ServiceApproval> getByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval WHERE userId = :userId ORDER BY approvalDate DESC", ServiceApproval.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las aprobaciones de un hospital
     */
    public List<ServiceApproval> getByHospitalId(Long hospitalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval WHERE hospitalId = :hospitalId ORDER BY approvalDate DESC", ServiceApproval.class);
            query.setParameter("hospitalId", hospitalId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene todas las aprobaciones
     */
    public List<ServiceApproval> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval ORDER BY approvalDate DESC", ServiceApproval.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generar un código de aprobación único
     */
    private String generateApprovalCode() {
        // Generar un UUID y tomar los primeros 8 caracteres
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "AP" + uuid;
    }

    /**
     * Obtiene todas las aprobaciones (alias para getAll para mantener compatibilidad)
     */
    public List<ServiceApproval> findAll() {
        return getAll();
    }

    /**
     * Obtiene una aprobación por su código (alias para getByApprovalCode para mantener compatibilidad)
     */
    public ServiceApproval findByApprovalCode(String approvalCode) {
        return getByApprovalCode(approvalCode);
    }

    /**
     * Obtiene una aprobación por su ID de receta
     */
    public ServiceApproval findByPrescriptionId(long prescriptionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval WHERE prescriptionId = :prescriptionId", ServiceApproval.class);
            query.setParameter("prescriptionId", prescriptionId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 