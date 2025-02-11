package com.ejemplo.dao;

import com.ejemplo.modelo.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.ejemplo.util.HibernateUtil;
import java.util.List;


public class UsuarioDAO {

    public List<Usuario> obtenerTodosLosUsuarios() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            //return session.createQuery("FROM Usuario", Usuario.class).list();
            return session.createQuery("SELECT * FROM LOLA").list();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obtener los usuarios");
            return null;
        }
    }

    public Usuario obtenerUsuarioPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Usuario.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obtener el usuarios");
            return null;
        }

    }

    public void cerrar() {
        HibernateUtil.getSessionFactory().close();
    }
}