package com.sources.app;

import com.sources.app.dao.UserDAO;
import com.sources.app.handlers.LoginHandler; // Importa el handler desde el paquete handlers
import com.sources.app.handlers.UserHandler;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpServer;
import org.hibernate.Session;

import java.net.InetSocketAddress;

public class App {
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) throws Exception {
        // Prueba de conexión a la base de datos
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (session.isConnected()) {
                System.out.println("Conexión exitosa a la base de datos!");
            } else {
                System.err.println("No se pudo establecer conexión a la base de datos.");
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos:");
            e.printStackTrace();
        }

        // Crear y configurar el servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/login", new LoginHandler(userDAO));
        server.createContext("/api/users", new UserHandler(userDAO));
        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("Servidor iniciado en http://localhost:8080/api");
    }
}
