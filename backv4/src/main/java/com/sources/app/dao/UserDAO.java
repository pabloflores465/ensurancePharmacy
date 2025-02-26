package com.sources.app.dao;

import com.sources.app.entities.User;
import com.sources.app.entities.Policy;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Date;

public class UserDAO {

    public User login(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email AND password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.uniqueResult(); // Retorna null si no encuentra el usuario
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método create modificado para asignar la relación con Policy
    public User create(String name, Long cui, String phone, String email, Date birthdate, String address, String password, Policy policy) {
        Transaction tx = null;
        User user = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Creamos una instancia de User y asignamos los valores, incluyendo la relación Policy
            user = new User();
            user.setName(name);
            user.setCui(cui);
            user.setPhone(phone);
            user.setEmail(email);
            user.setBirthDate(birthdate);
            user.setAddress(address);
            user.setPassword(password);
            user.setRole("usuario");
            user.setEnabled(1);
            user.setPolicy(policy);  // Asignamos el objeto Policy

            // Guardamos el usuario en la base de datos
            session.save(user);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return user;
    }

    // Método para obtener todos los usuarios (Read All)
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para obtener un usuario por su ID (Read by ID)
    public User findById(Long idUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, idUser);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
