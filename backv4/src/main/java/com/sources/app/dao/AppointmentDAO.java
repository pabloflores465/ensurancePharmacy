package com.sources.app.dao;

import com.sources.app.entities.Appointment;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.Date;
import java.util.List;

public class AppointmentDAO {

    public Appointment create(Long idHospital, Long idUser, Date appointmentDate, Integer enabled) {
        Transaction tx = null;
        Appointment appointment = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar las entidades Hospital y User usando sus identificadores
            Hospital hospital = session.get(Hospital.class, idHospital);
            User user = session.get(User.class, idUser);

            // Validamos que las entidades existan
            if (hospital == null || user == null) {
                throw new RuntimeException("Hospital or User not found");
            }

            // Creamos y configuramos la cita con las entidades recuperadas
            appointment = new Appointment();
            appointment.setHospital(hospital);
            appointment.setUser(user);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setEnabled(enabled);

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

    public Appointment findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Appointment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Appointment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery("FROM Appointment", Appointment.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Appointment update(Appointment appointment) {
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
}
