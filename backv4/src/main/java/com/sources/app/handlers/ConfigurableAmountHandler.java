package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.ConfigurableAmount;
import com.sources.app.dao.ConfigurableAmountDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ConfigurableAmountHandler implements HttpHandler {

    private final ConfigurableAmountDAO configDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT_CURRENT = "/api/configurable-amount/current";
    private static final String ENDPOINT_UPDATE = "/api/configurable-amount/update";

    public ConfigurableAmountHandler(ConfigurableAmountDAO configDAO) {
        this.configDAO = configDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.equals(ENDPOINT_CURRENT) && method.equalsIgnoreCase("GET")) {
                handleGetCurrentConfig(exchange);
            } else if (path.equals(ENDPOINT_UPDATE) && method.equalsIgnoreCase("PUT")) {
                handleUpdateConfig(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleGetCurrentConfig(HttpExchange exchange) throws IOException {
        ConfigurableAmount config = configDAO.findCurrentConfig();
        if (config == null) {
            config = new ConfigurableAmount();
            config.setPrescriptionAmount(new BigDecimal("250.00"));
        }
        String jsonResponse = objectMapper.writeValueAsString(config);
        sendJsonResponse(exchange, 200, jsonResponse);
    }

    private void handleUpdateConfig(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);

        BigDecimal newAmount = new BigDecimal(data.get("prescriptionAmount").toString());

        ConfigurableAmount currentConfig = configDAO.findCurrentConfig();
        if (currentConfig == null) {
            currentConfig = configDAO.create(newAmount);
        } else {
            currentConfig.setPrescriptionAmount(newAmount);
            currentConfig = configDAO.update(currentConfig);
        }

        if (currentConfig != null) {
            String jsonResponse = objectMapper.writeValueAsString(currentConfig);
            sendJsonResponse(exchange, 200, jsonResponse);
        } else {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            return new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
