package com.sources.app.dao;

import com.sources.app.entities.AppointmentMade;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class AppointmentMadeDAO {

    public AppointmentMade create(Long idCita, Long idUser, Date appointmentMadeDate) {
        Transaction tx = null;
        AppointmentMade appMade = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            appMade = new AppointmentMade();
            appMade.setIdCita(idCita);
            appMade.setIdUser(idUser);
            appMade.setAppointmentMadeDate(appointmentMadeDate);

            session.save(appMade);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return appMade;
    }

    public AppointmentMade findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(AppointmentMade.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AppointmentMade> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AppointmentMade> query = session.createQuery("FROM AppointmentMade", AppointmentMade.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AppointmentMade update(AppointmentMade appMade) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(appMade);
            tx.commit();
            return appMade;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
