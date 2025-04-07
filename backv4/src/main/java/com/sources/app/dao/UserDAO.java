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
            User user = query.uniqueResult();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsUserWithEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsUserWithCUI(Long cui) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.cui = :cui", Long.class);
            query.setParameter("cui", cui);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User create(String name, Long cui, String phone, String email, Date birthdate, String address, String password, Policy policy) {
        if (existsUserWithEmail(email)) {
            System.out.println("ERROR: Ya existe un usuario con el email: " + email);
            return null;
        }
        
        if (existsUserWithCUI(cui)) {
            System.out.println("ERROR: Ya existe un usuario con el CUI: " + cui);
            return null;
        }
        
        Transaction tx = null;
        Session session = null;
        User user = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            user = new User();
            user.setName(name);
            user.setCui(cui);
            user.setPhone(phone);
            user.setEmail(email);
            user.setBirthDate(birthdate);
            user.setAddress(address);
            user.setPassword(password);
            user.setRole(" ");
            user.setEnabled(0);
            user.setPolicy(policy);

            session.save(user);

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    rbEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return user;
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findById(Long idUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, idUser);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User update(User user) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            User existingUser = session.get(User.class, user.getIdUser());
            if (existingUser == null) {
                return null;
            }

            existingUser.setName(user.getName());
            existingUser.setCui(user.getCui());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setAddress(user.getAddress());
            existingUser.setBirthDate(user.getBirthDate());
            existingUser.setRole(user.getRole());
            existingUser.setEnabled(user.getEnabled());
            existingUser.setPassword(user.getPassword());

            if (user.getPolicy() != null && 
                (existingUser.getPolicy() == null || 
                 !existingUser.getPolicy().getIdPolicy().equals(user.getPolicy().getIdPolicy()))) {
                existingUser.setPolicy(user.getPolicy());
            }

            session.update(existingUser);
            tx.commit();
            
            return existingUser;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    rbEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Busca un usuario por su correo electrónico
     * @param email Correo electrónico del usuario
     * @return Usuario encontrado o null si no existe
     */
    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
