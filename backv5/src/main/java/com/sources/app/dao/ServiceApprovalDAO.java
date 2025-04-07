package com.sources.app.dao;

import com.sources.app.entities.Hospital;
import com.sources.app.entities.ServiceApproval;
import com.sources.app.entities.User;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ServiceApprovalDAO {

    // Crear una nueva aprobación de servicio
    public ServiceApproval create(User user, Hospital hospital, String serviceId, String serviceName,
                                String serviceDescription, Double serviceCost, Double coveredAmount,
                                Double patientAmount, String status) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            ServiceApproval approval = new ServiceApproval();
            approval.setUser(user);
            approval.setHospital(hospital);
            approval.setServiceId(serviceId);
            approval.setServiceName(serviceName);
            approval.setServiceDescription(serviceDescription);
            approval.setServiceCost(serviceCost);
            approval.setCoveredAmount(coveredAmount);
            approval.setPatientAmount(patientAmount);
            approval.setStatus(status);
            approval.setApprovalDate(new Date());
            
            // Generar código de aprobación único
            String approvalCode = generateApprovalCode();
            approval.setApprovalCode(approvalCode);
            
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
    
    // Actualizar la información de receta en una aprobación existente
    public ServiceApproval updatePrescription(Long approvalId, String prescriptionId, Double prescriptionTotal) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            ServiceApproval approval = session.get(ServiceApproval.class, approvalId);
            if (approval != null) {
                approval.setPrescriptionId(prescriptionId);
                approval.setPrescriptionTotal(prescriptionTotal);
                session.update(approval);
                transaction.commit();
                return approval;
            }
            return null;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    
    // Actualizar el estado de una aprobación
    public ServiceApproval updateStatus(Long approvalId, String status, String rejectionReason) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            ServiceApproval approval = session.get(ServiceApproval.class, approvalId);
            if (approval != null) {
                approval.setStatus(status);
                if (rejectionReason != null) {
                    approval.setRejectionReason(rejectionReason);
                }
                
                if ("COMPLETED".equals(status)) {
                    approval.setCompletedDate(new Date());
                }
                
                session.update(approval);
                transaction.commit();
                return approval;
            }
            return null;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    
    // Obtener una aprobación por su ID
    public ServiceApproval getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ServiceApproval.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Obtener una aprobación por su código
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
    
    // Obtener todas las aprobaciones de un usuario
    public List<ServiceApproval> getByUser(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval WHERE user.idUser = :userId ORDER BY createdAt DESC", ServiceApproval.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Obtener todas las aprobaciones de un hospital
    public List<ServiceApproval> getByHospital(Long hospitalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval WHERE hospital.idHospital = :hospitalId ORDER BY createdAt DESC", ServiceApproval.class);
            query.setParameter("hospitalId", hospitalId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Obtener todas las aprobaciones
    public List<ServiceApproval> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceApproval> query = session.createQuery(
                "FROM ServiceApproval ORDER BY createdAt DESC", ServiceApproval.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Generar un código de aprobación único
    private String generateApprovalCode() {
        // Generar un UUID y tomar los primeros 8 caracteres
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "AP" + uuid;
    }
} 