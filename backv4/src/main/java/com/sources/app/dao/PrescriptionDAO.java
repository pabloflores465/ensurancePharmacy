package com.sources.app.dao;

import com.sources.app.entities.Prescription;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;
import com.sources.app.entities.Medicine;
import com.sources.app.entities.Pharmacy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class PrescriptionDAO {

    public Prescription create(Long idHospital, Long idUser, Long idMedicine, Long idPharmacy,
                               Date prescriptionDate, BigDecimal total, BigDecimal copay,
                               String prescriptionComment, Integer secured, String auth) {
        Transaction tx = null;
        Prescription prescription = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Recuperar las entidades relacionadas
            Hospital hospital = session.get(Hospital.class, idHospital);
            User user = session.get(User.class, idUser);
            Medicine medicine = session.get(Medicine.class, idMedicine);
            Pharmacy pharmacy = session.get(Pharmacy.class, idPharmacy);

            if(hospital == null || user == null || medicine == null || pharmacy == null) {
                throw new RuntimeException("Alguna entidad relacionada no fue encontrada.");
            }

            prescription = new Prescription();
            prescription.setHospital(hospital);
            prescription.setUser(user);
            prescription.setMedicine(medicine);
            prescription.setPharmacy(pharmacy);
            prescription.setPrescriptionDate(prescriptionDate);
            prescription.setTotal(total);
            prescription.setCopay(copay);
            prescription.setPrescriptionComment(prescriptionComment);
            prescription.setSecured(secured);
            prescription.setAuth(auth);

            session.save(prescription);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return prescription;
    }

    public List<Prescription> findByUserId(Long idUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                    "FROM Prescription p WHERE p.user.idUser = :idUser", Prescription.class
            );
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Prescription findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Prescription.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Prescription> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery("FROM Prescription", Prescription.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Prescription update(Prescription prescription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(prescription);
            tx.commit();
            return prescription;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
