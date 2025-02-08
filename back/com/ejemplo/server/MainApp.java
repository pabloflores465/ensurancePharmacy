package com.ejemplo.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;


public class MainApp {
    public static void main(String[] args) throws Exception {
        // Crear el servidor en el puerto 8080
        Server server = new Server(8080);

        // Configurar el manejador de contexto
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Configurar el Servlet de Jersey
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer());
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.ejemplo.rest");

        // Añadir el servlet a la ruta "/api/*"
        context.addServlet(jerseyServlet, "/api/*");

        // Iniciar el servidor
        server.start();
        System.out.println("🚀 Servidor iniciado en http://localhost:8080/api/");
        server.join();
    }
}