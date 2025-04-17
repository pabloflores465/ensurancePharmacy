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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

public class UserHandler implements HttpHandler {
    private final UserDAO userDAO;
    private final ObjectMapper objectMapper;
    // Definimos el endpoint para este handler
    private static final String ENDPOINT = "/api/users";
    private static final Pattern ID_PATTERN = Pattern.compile(ENDPOINT + "/([0-9]+)");

    public UserHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Agregar encabezados CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
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

        // Verificar si es una solicitud de ID específico
        Matcher matcher = ID_PATTERN.matcher(path);
        boolean isIdRequest = matcher.matches();
        Long userId = null;
        
        if (isIdRequest) {
            try {
                userId = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
        }

        // Manejo de GET
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleGet(exchange, path, userId, isIdRequest);
        }
        // Manejo de POST (crear usuario)
        else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            handlePost(exchange);
        }
        // Manejo de PUT (actualizar usuario)
        else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            handlePut(exchange, userId);
        }
        // Si se utiliza otro método, se rechaza con 405 (Method Not Allowed)
        else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handleGet(HttpExchange exchange, String path, Long userId, boolean isIdRequest) throws IOException {
        // Si la URL es del tipo /api/users/{id} intentamos leer un usuario por id
        if (isIdRequest) {
            User user = userDAO.findById(userId);
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

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            // Leemos el cuerpo de la petición y lo convertimos a un objeto User
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            User createUser = objectMapper.readValue(requestBody, User.class);

            // Verificar si el correo ya existe
            if (userDAO.existsUserWithEmail(createUser.getEmail())) {
                String errorMessage = "El correo electrónico ya está registrado";
                Map<String, String> errorResponse = Map.of(
                    "error", "email_already_exists",
                    "message", errorMessage
                );
                
                String jsonResponse = objectMapper.writeValueAsString(errorResponse);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                return;
            }
            
            // Verificar si el CUI ya existe
            if (userDAO.existsUserWithCUI(createUser.getCui())) {
                String errorMessage = "El CUI/DPI ya está registrado";
                Map<String, String> errorResponse = Map.of(
                    "error", "cui_already_exists",
                    "message", errorMessage
                );
                
                String jsonResponse = objectMapper.writeValueAsString(errorResponse);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                return;
            }

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
                // Si no se pudo crear, devolvemos 400 Bad Request con un mensaje de error
                Map<String, String> errorResponse = Map.of(
                    "error", "user_creation_failed",
                    "message", "No se pudo crear el usuario. Verifique los datos e intente de nuevo."
                );
                
                String jsonResponse = objectMapper.writeValueAsString(errorResponse);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Devolver mensaje de error detallado
            Map<String, String> errorResponse = Map.of(
                "error", "server_error",
                "message", "Error en el servidor: " + e.getMessage()
            );
            
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private void handlePut(HttpExchange exchange, Long userId) throws IOException {
        if (userId == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        try {
            // Verificar si el usuario existe
            User existingUser = userDAO.findById(userId);
            if (existingUser == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            // Leer el cuerpo de la petición
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            User updatedUser = objectMapper.readValue(requestBody, User.class);
            
            // Asegurarse de que el ID sea el mismo
            updatedUser.setIdUser(userId);
            
            // Reglas de negocio para la actualización
            // 1. Si paidService es false, policy debe ser null
            if (updatedUser.getPaidService() != null && !updatedUser.getPaidService()) {
                updatedUser.setPolicy(null);
                updatedUser.setExpirationDate(null);
            }
            
            // 2. Si paidService es true y expirationDate es null, se podría establecer una fecha predeterminada
            // (esta lógica se maneja en UserDAO)
            
            // Actualizar el usuario
            User result = userDAO.update(updatedUser);
            
            if (result != null) {
                // Si se actualizó correctamente, devolvemos el objeto actualizado
                String jsonResponse = objectMapper.writeValueAsString(result);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } else {
                // Si no se pudo actualizar
                exchange.sendResponseHeaders(500, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
