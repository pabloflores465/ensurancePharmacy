package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.AppointmentDAO;
import com.sources.app.entities.Appointment;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppointmentHandler implements HttpHandler {

    private final AppointmentDAO appointmentDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/appointment";

    public AppointmentHandler(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!path.equalsIgnoreCase(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        switch (exchange.getRequestMethod().toUpperCase()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("id=")) {
            Map<String, String> params = parseQuery(query);
            try {
                Long id = Long.parseLong(params.get("id"));
                Appointment appointment = appointmentDAO.findById(id);
                if (appointment != null) {
                    String jsonResponse = objectMapper.writeValueAsString(appointment);
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
            List<Appointment> appointments = appointmentDAO.findAll();
            String jsonResponse = objectMapper.writeValueAsString(appointments);
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
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Appointment appointment = objectMapper.readValue(requestBody, Appointment.class);

            // Extraer los identificadores de Hospital y User desde las relaciones
            Hospital hospital = appointment.getHospital();
            User user = appointment.getUser();
            if (hospital == null || user == null) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            Appointment createdAppointment = appointmentDAO.create(
                    hospital.getIdHospital(),  // Se asume que Hospital tiene getIdHospital()
                    user.getIdUser(),          // Se asume que User tiene getIdUser()
                    appointment.getAppointmentDate(),
                    appointment.getEnabled()
            );
            if (createdAppointment != null) {
                String jsonResponse = objectMapper.writeValueAsString(createdAppointment);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(201, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } else {
                exchange.sendResponseHeaders(500, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Appointment appointment = objectMapper.readValue(requestBody, Appointment.class);
        Appointment updatedAppointment = appointmentDAO.update(appointment);
        if (updatedAppointment != null) {
            String jsonResponse = objectMapper.writeValueAsString(updatedAppointment);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } else {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        kv -> kv[0],
                        kv -> kv.length > 1 ? kv[1] : ""
                ));
    }
}
