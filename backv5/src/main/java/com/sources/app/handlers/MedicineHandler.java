package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Medicine;
import com.sources.app.dao.MedicineDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MedicineHandler implements HttpHandler {
    private final MedicineDAO medicineDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/medicines";

    public MedicineHandler(MedicineDAO medicineDAO) {
        this.medicineDAO = medicineDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!path.startsWith(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange, path);
            } else if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            } else if ("PUT".equalsIgnoreCase(method)) {
                handlePut(exchange, path);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        // Si la ruta es exactamente el endpoint, se devuelven todas las medicinas
        if (path.equalsIgnoreCase(ENDPOINT)) {
            List<Medicine> medicines = medicineDAO.getAll();
            String jsonResponse = objectMapper.writeValueAsString(medicines);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } else {
            // Se asume la ruta /api2/medicines/{id}
            String[] parts = path.split("/");
            if (parts.length == 4) {
                try {
                    Long id = Long.parseLong(parts[3]);
                    Medicine medicine = medicineDAO.getById(id);
                    if (medicine != null) {
                        String jsonResponse = objectMapper.writeValueAsString(medicine);
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
                exchange.sendResponseHeaders(400, -1);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Medicine createMed = objectMapper.readValue(requestBody, Medicine.class);
        Medicine medicine = medicineDAO.create(
                createMed.getName(),
                createMed.getActiveMedicament(),
                createMed.getDescription(),
                createMed.getImage(),
                createMed.getConcentration(),
                createMed.getPresentacion(),
                createMed.getStock(),
                createMed.getBrand(),
                createMed.getPrescription(),
                createMed.getPrice(),
                createMed.getSoldUnits()
        );
        if (medicine != null) {
            String jsonResponse = objectMapper.writeValueAsString(medicine);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(201, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        // Se asume la ruta /api2/medicines/{id} para actualizar
        String[] parts = path.split("/");
        if (parts.length == 4) {
            try {
                Long id = Long.parseLong(parts[3]);
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Medicine updateMed = objectMapper.readValue(requestBody, Medicine.class);
                updateMed.setIdMedicine(id);
                Medicine updatedMedicine = medicineDAO.update(updateMed);
                if (updatedMedicine != null) {
                    String jsonResponse = objectMapper.writeValueAsString(updatedMedicine);
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
            exchange.sendResponseHeaders(400, -1);
        }
    }
}
