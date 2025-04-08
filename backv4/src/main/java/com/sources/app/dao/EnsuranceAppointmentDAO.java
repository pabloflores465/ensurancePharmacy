package com.sources.app.dao;

import com.sources.app.entities.EnsuranceAppointment;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

/**
 * DAO para operaciones CRUD en la entidad EnsuranceAppointment
 */
public class EnsuranceAppointmentDAO {

    /**
     * Crea una nueva cita en el sistema de seguros
     * @param hospitalAppointmentId ID de la cita en el sistema del hospital
     * @param idUser ID del usuario en el sistema de seguros
     * @param appointmentDate Fecha de la cita
     * @param doctorName Nombre del doctor (opcional)
     * @param reason Motivo de la cita (opcional)
     * @return La cita creada o null si hubo un error
     */
    public EnsuranceAppointment create(String hospitalAppointmentId, Long idUser, Date appointmentDate, String doctorName, String reason) {
        Transaction tx = null;
        EnsuranceAppointment appointment = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            appointment = new EnsuranceAppointment();
            appointment.setHospitalAppointmentId(hospitalAppointmentId);
            appointment.setIdUser(idUser);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setDoctorName(doctorName);
            appointment.setReason(reason);

            session.save(appointment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return appointment;
    }

    /**
     * Busca una cita por su ID interno
     * @param id ID de la cita en el sistema de seguros
     * @return La cita encontrada o null si no existe
     */
    public EnsuranceAppointment findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(EnsuranceAppointment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca una cita por su ID en el sistema del hospital
     * @param hospitalAppointmentId ID de la cita en el sistema del hospital
     * @return La cita encontrada o null si no existe
     */
    public EnsuranceAppointment findByHospitalAppointmentId(String hospitalAppointmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<EnsuranceAppointment> query = session.createQuery(
                    "FROM EnsuranceAppointment WHERE hospitalAppointmentId = :hospitalId",
                    EnsuranceAppointment.class
            );
            query.setParameter("hospitalId", hospitalAppointmentId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca todas las citas de un usuario
     * @param idUser ID del usuario en el sistema de seguros
     * @return Lista de citas del usuario
     */
    public List<EnsuranceAppointment> findByUserId(Long idUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<EnsuranceAppointment> query = session.createQuery(
                    "FROM EnsuranceAppointment WHERE idUser = :userId ORDER BY appointmentDate DESC",
                    EnsuranceAppointment.class
            );
            query.setParameter("userId", idUser);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene todas las citas
     * @return Lista de todas las citas
     */
    public List<EnsuranceAppointment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<EnsuranceAppointment> query = session.createQuery(
                    "FROM EnsuranceAppointment ORDER BY appointmentDate DESC",
                    EnsuranceAppointment.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza una cita existente
     * @param appointment Cita a actualizar
     * @return La cita actualizada o null si hubo un error
     */
    public EnsuranceAppointment update(EnsuranceAppointment appointment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(appointment);
            tx.commit();
            return appointment;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina una cita por su ID
     * @param id ID de la cita a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            EnsuranceAppointment appointment = session.get(EnsuranceAppointment.class, id);
            if (appointment != null) {
                session.delete(appointment);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una cita por su ID en el sistema del hospital
     * @param hospitalAppointmentId ID de la cita en el sistema del hospital
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean deleteByHospitalAppointmentId(String hospitalAppointmentId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Query<EnsuranceAppointment> query = session.createQuery(
                    "FROM EnsuranceAppointment WHERE hospitalAppointmentId = :hospitalId",
                    EnsuranceAppointment.class
            );
            query.setParameter("hospitalId", hospitalAppointmentId);
            EnsuranceAppointment appointment = query.uniqueResult();
            
            if (appointment != null) {
                session.delete(appointment);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
} 