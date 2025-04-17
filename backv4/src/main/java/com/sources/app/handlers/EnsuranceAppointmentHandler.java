package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.EnsuranceAppointmentDAO;
import com.sources.app.entities.EnsuranceAppointment;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handler para gestionar las solicitudes HTTP relacionadas con las citas en el sistema de seguros
 */
public class EnsuranceAppointmentHandler implements HttpHandler {

    private final EnsuranceAppointmentDAO appointmentDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/ensurance-appointments";

    /**
     * Constructor
     * @param appointmentDAO DAO para operaciones en citas
     */
    public EnsuranceAppointmentHandler(EnsuranceAppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Configurar CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No content for OPTIONS
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    if (query != null && query.contains("hospitalId=")) {
                        // Solicitud para buscar cita por ID de hospital
                        handleGetByHospitalId(exchange, query);
                    } else if (query != null && query.contains("today=true")) {
                        // Solicitud para obtener citas de hoy
                        handleGetTodayAppointments(exchange);
                    } else if (query != null && query.contains("date=")) {
                        // Solicitud para obtener citas por fecha específica
                        handleGetByDate(exchange, query);
                    } else if (query != null && query.contains("userId=")) {
                        // Solicitud para obtener citas por ID de usuario
                        handleGetByUserId(exchange, query);
                    } else {
                        // Obtener todas las citas
                        handleGetAll(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (query != null && query.contains("hospitalId=")) {
                        // Eliminar cita por ID de hospital
                        handleDeleteByHospitalId(exchange, query);
                    } else {
                        exchange.sendResponseHeaders(400, -1); // Bad Request
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }

    /**
     * Maneja las solicitudes GET para obtener todas las citas
     */
    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<EnsuranceAppointment> appointments = appointmentDAO.findAll();
        String response = objectMapper.writeValueAsString(appointments);
        sendJsonResponse(exchange, 200, response);
    }

    /**
     * Maneja las solicitudes GET para obtener una cita por su ID
     */
    private void handleGetById(HttpExchange exchange, Long id) throws IOException {
        EnsuranceAppointment appointment = appointmentDAO.findById(id);
        if (appointment != null) {
            String response = objectMapper.writeValueAsString(appointment);
            sendJsonResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(404, -1); // Not found
        }
    }

    /**
     * Maneja las solicitudes GET para obtener citas por ID de usuario
     */
    private void handleGetByUserId(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        try {
            Long userId = Long.parseLong(params.get("userId"));
            List<EnsuranceAppointment> appointments = appointmentDAO.findByUserId(userId);
            String response = objectMapper.writeValueAsString(appointments);
            sendJsonResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, -1); // Bad request
        }
    }

    /**
     * Maneja las solicitudes GET para obtener citas por ID de hospital
     */
    private void handleGetByHospitalId(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        String hospitalId = params.get("hospitalId");
        EnsuranceAppointment appointment = appointmentDAO.findByHospitalAppointmentId(hospitalId);
        if (appointment != null) {
            String response = objectMapper.writeValueAsString(appointment);
            sendJsonResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(404, -1); // Not found
        }
    }

    /**
     * Maneja las solicitudes POST para crear una nueva cita
     */
    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            // Leer el cuerpo de la solicitud
            String requestBody = readRequestBody(exchange);
            
            // Depurar el cuerpo recibido
            System.out.println("Cuerpo de la solicitud: " + requestBody);
            
            // Intentar parsear como un mapa para mayor flexibilidad
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
            
            // Extraer datos
            String hospitalAppointmentId = (String) data.get("hospitalAppointmentId");
            Long idUser = Long.valueOf(data.get("idUser").toString());
            Date appointmentDate;
            
            // Intentar parsear la fecha
            try {
                String dateStr = (String) data.get("appointmentDate");
                appointmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            } catch (Exception e) {
                appointmentDate = new Date(); // Usar fecha actual si hay error
            }
            
            String doctorName = (String) data.get("doctorName");
            String reason = (String) data.get("reason");
            
            // Crear la cita
            EnsuranceAppointment appointment = appointmentDAO.create(
                    hospitalAppointmentId,
                    idUser,
                    appointmentDate,
                    doctorName,
                    reason
            );
            
            if (appointment != null) {
                String response = objectMapper.writeValueAsString(appointment);
                sendJsonResponse(exchange, 201, response); // Created
            } else {
                exchange.sendResponseHeaders(500, -1); // Internal server error
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al crear cita: " + e.getMessage());
            exchange.sendResponseHeaders(400, -1); // Bad request
        }
    }

    /**
     * Maneja las solicitudes PUT para actualizar una cita existente
     */
    private void handlePut(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            EnsuranceAppointment appointment = objectMapper.readValue(requestBody, EnsuranceAppointment.class);
            EnsuranceAppointment updated = appointmentDAO.update(appointment);
            
            if (updated != null) {
                String response = objectMapper.writeValueAsString(updated);
                sendJsonResponse(exchange, 200, response);
            } else {
                exchange.sendResponseHeaders(500, -1); // Internal server error
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(400, -1); // Bad request
        }
    }

    /**
     * Maneja las solicitudes DELETE para eliminar una cita por ID
     */
    private void handleDeleteById(HttpExchange exchange, Long id) throws IOException {
        boolean deleted = appointmentDAO.delete(id);
        if (deleted) {
            sendJsonResponse(exchange, 200, "{\"deleted\": true}");
        } else {
            exchange.sendResponseHeaders(404, -1); // Not found
        }
    }

    /**
     * Maneja solicitudes DELETE para eliminar citas por ID del hospital
     */
    private void handleDeleteByHospitalId(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQueryParams(query);
        String hospitalId = params.get("hospitalId");
        
        if (hospitalId == null || hospitalId.trim().isEmpty()) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }
        
        boolean deleted = appointmentDAO.deleteByHospitalAppointmentId(hospitalId);
        
        if (deleted) {
            exchange.sendResponseHeaders(204, -1); // No Content
        } else {
            exchange.sendResponseHeaders(404, -1); // Not Found
        }
    }

    /**
     * Método auxiliar para enviar respuestas JSON
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Método auxiliar para leer el cuerpo de una solicitud
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            return new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Método auxiliar para parsear los parámetros de consulta
     */
    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param.length > 1 ? param[1] : ""
                ));
    }

    /**
     * Maneja solicitudes GET para obtener citas del día actual
     */
    private void handleGetTodayAppointments(HttpExchange exchange) throws IOException {
        List<EnsuranceAppointment> appointments = appointmentDAO.findTodayAppointments();
        
        if (appointments != null) {
            // Obtener información adicional de cada usuario para las citas
            appointments = enrichAppointmentsWithUserInfo(appointments);
            
            String response = objectMapper.writeValueAsString(appointments);
            sendJsonResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }
    
    /**
     * Maneja solicitudes GET para obtener citas por fecha específica
     */
    private void handleGetByDate(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQueryParams(query);
        String dateStr = params.get("date");
        
        if (dateStr == null) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }
        
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            List<EnsuranceAppointment> appointments = appointmentDAO.findByDate(date);
            
            if (appointments != null) {
                // Obtener información adicional de cada usuario para las citas
                appointments = enrichAppointmentsWithUserInfo(appointments);
                
                String response = objectMapper.writeValueAsString(appointments);
                sendJsonResponse(exchange, 200, response);
            } else {
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(400, -1); // Bad Request
        }
    }
    
    /**
     * Enriquece las citas con información del usuario
     */
    private List<EnsuranceAppointment> enrichAppointmentsWithUserInfo(List<EnsuranceAppointment> appointments) {
        // Aquí podríamos obtener información adicional de cada usuario
        // como nombre, email, etc. desde la base de datos
        // Por ahora simplemente devuelve la lista original
        return appointments;
    }
    
    /**
     * Parsea los parámetros de la URL
     */
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        
        if (query == null || query.isEmpty()) {
            return params;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                params.put(key, value);
            }
        }
        
        return params;
    }
} 