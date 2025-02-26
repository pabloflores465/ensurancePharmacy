package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.UserDAO;
import com.sources.app.entities.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserHandler implements HttpHandler {
    private final UserDAO userDAO;
    private final ObjectMapper objectMapper;
    // Definimos el endpoint para este handler
    private static final String ENDPOINT = "/api/users";

    public UserHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Agregar encabezados CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Manejar solicitud preflight (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Sin contenido
            return;
        }

        // Obtenemos la ruta solicitada
        String path = exchange.getRequestURI().getPath();

        // Si la ruta no comienza con el endpoint, devolvemos 404
        if (!path.startsWith(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // Manejo de GET
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Si la URL es del tipo /api/users/{id} intentamos leer un usuario por id
            String[] parts = path.split("/");
            if (parts.length == 4) {
                try {
                    Long id = Long.parseLong(parts[3]);
                    User user = userDAO.findById(id);
                    if (user != null) {
                        String jsonResponse = objectMapper.writeValueAsString(user);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBytes);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1);
                }
            } else {
                // Si no se especifica un id, devolvemos todos los usuarios
                List<User> users = userDAO.findAll();
                String jsonResponse = objectMapper.writeValueAsString(users);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        }
        // Manejo de POST (crear usuario)
        else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // Leemos el cuerpo de la petición y lo convertimos a un objeto User
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                User createUser = objectMapper.readValue(requestBody, User.class);

                // Llamamos al método create del DAO, ahora incluyendo el objeto Policy
                User user = userDAO.create(
                        createUser.getName(),
                        createUser.getCui(),
                        createUser.getPhone(),
                        createUser.getEmail(),
                        createUser.getBirthDate(),
                        createUser.getAddress(),
                        createUser.getPassword(),
                        createUser.getPolicy() // Se espera que en el JSON se incluya la información de la Policy
                );

                if (user != null) {
                    // Si se creó el usuario correctamente, devolvemos el objeto en formato JSON
                    String jsonResponse = objectMapper.writeValueAsString(user);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    // Se usa 201 (Created) para indicar que se creó un recurso
                    exchange.sendResponseHeaders(201, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    // Si no se pudo crear, devolvemos 400 Bad Request
                    exchange.sendResponseHeaders(400, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        // Si se utiliza otro método, se rechaza con 405 (Method Not Allowed)
        else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
