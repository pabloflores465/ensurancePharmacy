package com.sources.app.handlers;

import com.sources.app.dao.ExternalMedicineDAO;
import com.sources.app.entities.Medicine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExternalMedicineHandler implements HttpHandler {
    private ExternalMedicineDAO externalMedicineDAO;
    private ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/external_medicines";

    public ExternalMedicineHandler(ExternalMedicineDAO externalMedicineDAO) {
        this.externalMedicineDAO = externalMedicineDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        try {
            if (exchange.getRequestMethod().equals("GET")) {
                handleGet(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
    
    private void handleGet(HttpExchange exchange) throws IOException {
        List<Medicine> medicines = externalMedicineDAO.getAll();
        String jsonResponse = objectMapper.writeValueAsString(medicines);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
