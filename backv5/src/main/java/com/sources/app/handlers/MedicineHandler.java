package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Medicine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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
        // Encabezados CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Verifica la ruta
        String path = exchange.getRequestURI().getPath();
        if (!path.equalsIgnoreCase(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // Solo POST para crear una medicina
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                // Se espera un JSON con: name, activeMedicament, description, image, concentration, presentacion, stock, brand, prescription, price, soldUnits
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
