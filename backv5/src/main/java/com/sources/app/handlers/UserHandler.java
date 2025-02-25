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
    private static final String ENDPOINT = "/api2/users";

    public UserHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Encabezados CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Manejo de preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Verificar endpoint (se asume que la ruta es correcta)
        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method)) {
            // CREATE
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                User createUser = objectMapper.readValue(requestBody, User.class);
                User user = userDAO.create(
                        createUser.getName(),
                        createUser.getCui(),
                        createUser.getPhone(),
                        createUser.getEmail(),
                        createUser.getBirthDate(),
                        createUser.getAddress(),
                        createUser.getPassword()
                );
                if (user != null) {
                    String jsonResponse = objectMapper.writeValueAsString(user);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(201, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else if ("GET".equalsIgnoreCase(method)) {
            // READ: Si se envía ?id= valor, se retorna un usuario; si no, todos
            try {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("id=")) {
                    Long id = Long.parseLong(query.substring(3));
                    User user = userDAO.getById(id);
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
                } else {
                    List<User> users = userDAO.getAll();
                    String jsonResponse = objectMapper.writeValueAsString(users);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else if ("PUT".equalsIgnoreCase(method)) {
            // UPDATE
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                User updateUser = objectMapper.readValue(requestBody, User.class);
                User user = userDAO.update(updateUser);
                if (user != null) {
                    String jsonResponse = objectMapper.writeValueAsString(user);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try(OutputStream os = exchange.getResponseBody()){
                        os.write(responseBytes);
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
            } catch(Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
