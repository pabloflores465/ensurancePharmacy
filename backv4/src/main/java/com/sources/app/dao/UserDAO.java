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
            
            // Valores por defecto para los nuevos campos
            user.setPaidService(null); // Inicialmente nulo, sin valor definido
            user.setExpirationDate(null); // Sin fecha de expiración

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
            
            // Actualizar campos de servicio
            existingUser.setPaidService(user.getPaidService());
            existingUser.setExpirationDate(user.getExpirationDate());

            // Verificar si hay que limpiar la fecha de expiración
            if (user.getPaidService() != null && !user.getPaidService()) {
                existingUser.setExpirationDate(null); // Si no tiene servicio pagado, no tiene fecha de expiración
            }

            // Verificar expiración del servicio
            checkServiceExpiration(existingUser);

            // Actualizar política según el servicio
            if (user.getPolicy() != null && 
                (existingUser.getPolicy() == null || 
                 !existingUser.getPolicy().getIdPolicy().equals(user.getPolicy().getIdPolicy()))) {
                existingUser.setPolicy(user.getPolicy());
            } else if (user.getPolicy() == null || 
                      (user.getPaidService() != null && !user.getPaidService())) {
                // Si el usuario ya no tiene póliza asignada o el servicio no está pagado
                existingUser.setPolicy(null);
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
     * Verifica si el servicio del usuario ha expirado y actualiza su estado
     * @param user Usuario a verificar
     */
    private void checkServiceExpiration(User user) {
        // Ignorar usuarios sin servicio definido o sin fecha de expiración
        if (user.getPaidService() == null || user.getExpirationDate() == null) {
            return;
        }
        
        // Solo verificar expiración si el servicio está pagado
        if (user.getPaidService()) {
            Date today = new Date();
            if (user.getExpirationDate().before(today)) {
                // El servicio ha expirado
                user.setPaidService(false);
                user.setPolicy(null); // Quitar la póliza asignada
                System.out.println("Servicio expirado para el usuario: " + user.getEmail());
            }
        }
    }
    
    /**
     * Verifica y actualiza el estado de expiración de todos los usuarios
     * @return Número de usuarios actualizados
     */
    public int checkAllUsersServiceExpiration() {
        int updatedCount = 0;
        Transaction tx = null;
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            // Buscar usuarios con servicio pagado y fecha de expiración anterior a hoy
            Date today = new Date();
            Query<User> query = session.createQuery(
                "FROM User WHERE paidService = true AND expirationDate < :today", 
                User.class
            );
            query.setParameter("today", today);
            List<User> expiredUsers = query.getResultList();
            
            for (User user : expiredUsers) {
                user.setPaidService(false);
                user.setPolicy(null);
                session.update(user);
                updatedCount++;
            }
            
            tx.commit();
            System.out.println("Servicios expirados actualizados: " + updatedCount);
            return updatedCount;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    rbEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return 0;
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
