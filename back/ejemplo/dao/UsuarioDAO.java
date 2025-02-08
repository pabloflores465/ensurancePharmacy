package com.ejemplo.dao;

import com.ejemplo.model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.ejemplo.util.HibernateUtil;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> obtenerTodosLosUsuarios() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario", Usuario.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Usuario obtenerUsuarioPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Usuario.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}