package com.sources.app.dao;

import com.sources.app.entities.User;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class UserDAO {

    // Método para login: busca por email y password
    public User login(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User WHERE email = :email AND password = :password", User.class
            );
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.uniqueResult(); // Retorna null si no encuentra el usuario
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para crear un usuario
    public User create(String name, String cui, String phone, String email, Date birthDate, String address, String password) {
        Transaction tx = null;
        User user = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            user = new User();
            user.setName(name);
            user.setCui(cui);
            user.setPhone(phone);
            user.setEmail(email);
            user.setBirthDate(birthDate);
            user.setAddress(address);
            user.setPassword(password);
            user.setRole("usuario");
            user.setEnabled('1'); // Se usa '1' como Character

            session.save(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return user;
    }

    // Opcional: otros métodos CRUD (READ ALL, GET BY ID, UPDATE)
    public List<User> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
