package com.ejemplo.test;

import com.ejemplo.dao.UsuarioDAO;
import com.ejemplo.modelo.Usuario;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios();
            if (usuarios != null && !usuarios.isEmpty()) {
                for (Usuario usuario : usuarios) {
                    System.out.println(usuario);
                }
            } else {
                System.out.println("⚠️ No se encontraron usuarios en la base de datos.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
          usuarioDAO.cerrar(); // Asegura el cierre de la conexión
        }
    }
}