package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.InsuranceServiceDAO;
import com.sources.app.dao.CategoryDAO;
import com.sources.app.entities.InsuranceService;
import com.sources.app.entities.Category;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class InsuranceServiceHandler implements HttpHandler {
    private final InsuranceServiceDAO insuranceServiceDAO;
    private final CategoryDAO categoryDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/insurance-services";

    public InsuranceServiceHandler(InsuranceServiceDAO insuranceServiceDAO, CategoryDAO categoryDAO) {
        this.insuranceServiceDAO = insuranceServiceDAO;
        this.categoryDAO = categoryDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS Headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        // Verificar que la ruta comience con el endpoint
        if (!path.startsWith(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        try {
            // GET para obtener todos los servicios o filtrar por categoría
            if ("GET".equals(exchange.getRequestMethod())) {
                if (path.equals(ENDPOINT)) {
                    handleGetAll(exchange, query);
                } else {
                    // Extraer ID del servicio de la ruta
                    String idStr = path.substring(ENDPOINT.length() + 1);
                    if (idStr.contains("/")) {
                        idStr = idStr.substring(0, idStr.indexOf("/"));
                    }
                    try {
                        Long id = Long.parseLong(idStr);
                        handleGetById(exchange, id);
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
            } 
            // POST para crear un nuevo servicio
            else if ("POST".equals(exchange.getRequestMethod()) && path.equals(ENDPOINT)) {
                handleCreate(exchange);
            } 
            // PUT para actualizar un servicio existente
            else if ("PUT".equals(exchange.getRequestMethod()) && path.startsWith(ENDPOINT + "/")) {
                String idStr = path.substring(ENDPOINT.length() + 1);
                try {
                    Long id = Long.parseLong(idStr);
                    handleUpdate(exchange, id);
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1);
                }
            } 
            // DELETE para eliminar un servicio
            else if ("DELETE".equals(exchange.getRequestMethod()) && path.startsWith(ENDPOINT + "/")) {
                String idStr = path.substring(ENDPOINT.length() + 1);
                try {
                    Long id = Long.parseLong(idStr);
                    handleDelete(exchange, id);
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleGetAll(HttpExchange exchange, String query) throws IOException {
        List<InsuranceService> services;
        
        if (query != null && query.startsWith("category=")) {
            try {
                Long categoryId = Long.parseLong(query.substring("category=".length()));
                Category category = categoryDAO.findById(categoryId);
                if (category != null) {
                    services = insuranceServiceDAO.findByCategory(category);
                } else {
                    services = insuranceServiceDAO.findAll();
                }
            } catch (NumberFormatException e) {
                services = insuranceServiceDAO.findAll();
            }
        } else if (query != null && query.startsWith("subcategory=")) {
            try {
                Long subcategoryId = Long.parseLong(query.substring("subcategory=".length()));
                Category subcategory = categoryDAO.findById(subcategoryId);
                if (subcategory != null) {
                    services = insuranceServiceDAO.findBySubcategory(subcategory);
                } else {
                    services = insuranceServiceDAO.findAll();
                }
            } catch (NumberFormatException e) {
                services = insuranceServiceDAO.findAll();
            }
        } else {
            services = insuranceServiceDAO.findAll();
        }
        
        String response = objectMapper.writeValueAsString(services);
        sendResponse(exchange, 200, response);
    }

    private void handleGetById(HttpExchange exchange, Long id) throws IOException {
        InsuranceService service = insuranceServiceDAO.findById(id);
        
        if (service != null) {
            String response = objectMapper.writeValueAsString(service);
            sendResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        InsuranceService service = new InsuranceService();
        service.setName((String) data.get("name"));
        service.setDescription((String) data.get("description"));
        
        // Obtener categoría
        Long categoryId = Long.valueOf(data.get("categoryId").toString());
        Category category = categoryDAO.findById(categoryId);
        
        // Obtener subcategoría
        Long subcategoryId = Long.valueOf(data.get("subcategoryId").toString());
        Category subcategory = categoryDAO.findById(subcategoryId);
        
        if (category == null || subcategory == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        
        service.setCategory(category);
        service.setSubcategory(subcategory);
        service.setPrice(Double.valueOf(data.get("price").toString()));
        service.setCoveragePercentage(Integer.valueOf(data.get("coveragePercentage").toString()));
        service.setEnabled(Integer.valueOf(data.get("enabled").toString()));
        
        InsuranceService created = insuranceServiceDAO.create(service);
        
        if (created != null) {
            String response = objectMapper.writeValueAsString(created);
            sendResponse(exchange, 201, response);
        } else {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleUpdate(HttpExchange exchange, Long id) throws IOException {
        InsuranceService existingService = insuranceServiceDAO.findById(id);
        
        if (existingService == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        if (data.containsKey("name")) {
            existingService.setName((String) data.get("name"));
        }
        
        if (data.containsKey("description")) {
            existingService.setDescription((String) data.get("description"));
        }
        
        if (data.containsKey("categoryId")) {
            Long categoryId = Long.valueOf(data.get("categoryId").toString());
            Category category = categoryDAO.findById(categoryId);
            if (category != null) {
                existingService.setCategory(category);
            }
        }
        
        if (data.containsKey("subcategoryId")) {
            Long subcategoryId = Long.valueOf(data.get("subcategoryId").toString());
            Category subcategory = categoryDAO.findById(subcategoryId);
            if (subcategory != null) {
                existingService.setSubcategory(subcategory);
            }
        }
        
        if (data.containsKey("price")) {
            existingService.setPrice(Double.valueOf(data.get("price").toString()));
        }
        
        if (data.containsKey("coveragePercentage")) {
            existingService.setCoveragePercentage(Integer.valueOf(data.get("coveragePercentage").toString()));
        }
        
        if (data.containsKey("enabled")) {
            existingService.setEnabled(Integer.valueOf(data.get("enabled").toString()));
        }
        
        InsuranceService updated = insuranceServiceDAO.update(existingService);
        
        if (updated != null) {
            String response = objectMapper.writeValueAsString(updated);
            sendResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleDelete(HttpExchange exchange, Long id) throws IOException {
        boolean success = insuranceServiceDAO.delete(id);
        
        if (success) {
            sendResponse(exchange, 200, "{\"success\":true}");
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
} 