package com.sources.app.dao;

import com.sources.app.entities.User;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class VerificationDAO {
    public boolean verifyUser(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            System.out.println("Buscando usuario con email: " + email);
            Query<User> query = session.createQuery("FROM User WHERE email = :email AND role = 'externo'", User.class);
            query.setParameter("email", email);
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                System.out.println("Usuario no encontrado");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al verificar el usuario");
            return false;
        }
    }
}
