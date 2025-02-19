package com.sources.app.dao;

import com.sources.app.entities.User;
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
    public User create(String name, Long cui, String phone, String email, Date birthdate, String address, String password) {
        Transaction tx = null;
        User user = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Creamos una instancia de User y asignamos los valores
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

            // Guardamos el usuario en la base de datos
            session.save(user);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return user;
    }

}
