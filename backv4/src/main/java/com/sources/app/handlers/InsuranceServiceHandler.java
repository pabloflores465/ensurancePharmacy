package com.sources.app.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.InsuranceServiceDAO;
import com.sources.app.dao.CategoryDAO;
import com.sources.app.entities.InsuranceService;
import com.sources.app.entities.Category;
import com.sources.app.util.HttpClientUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsuranceServiceHandler implements HttpHandler {
    private final InsuranceServiceDAO insuranceServiceDAO;
    private final CategoryDAO categoryDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/insurance-services";
    private static final String HOSPITAL_API_BASE_URL = "http://localhost:8000/api";

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
            // Endpoint para importar servicios del hospital
            if ("GET".equals(exchange.getRequestMethod()) && path.equals(ENDPOINT + "/hospital-services")) {
                handleGetHospitalServices(exchange);
                return;
            }
            
            // Endpoint para aprobar un servicio del hospital
            if ("POST".equals(exchange.getRequestMethod()) && path.equals(ENDPOINT + "/approve-hospital-service")) {
                handleApproveHospitalService(exchange);
                return;
            }
            
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

    private void handleGetHospitalServices(HttpExchange exchange) throws IOException {
        try {
            // Obtener servicios del hospital
            String servicesJson = HttpClientUtil.get(HOSPITAL_API_BASE_URL + "/services/");
            
            if (servicesJson == null) {
                sendResponse(exchange, 500, "{\"error\":\"No se pudieron obtener los servicios del hospital\"}");
                return;
            }
            
            // Procesar la respuesta JSON
            JsonNode servicesNode = objectMapper.readTree(servicesJson);
            JsonNode servicesArray = servicesNode.get("services");
            
            if (servicesArray == null || !servicesArray.isArray()) {
                sendResponse(exchange, 500, "{\"error\":\"Formato de respuesta del hospital inválido\"}");
                return;
            }
            
            List<Map<String, Object>> responseList = new ArrayList<>();
            
            // Procesar cada servicio
            for (JsonNode serviceNode : servicesArray) {
                Map<String, Object> serviceMap = new HashMap<>();
                serviceMap.put("hospitalServiceId", serviceNode.get("_id").asText());
                serviceMap.put("name", serviceNode.get("name").asText());
                
                // Verificar si el servicio ya está importado
                boolean isImported = false;
                String externalId = serviceNode.get("_id").asText();
                List<InsuranceService> existingServices = insuranceServiceDAO.findByExternalId(externalId);
                if (existingServices != null && !existingServices.isEmpty()) {
                    isImported = true;
                    serviceMap.put("insuranceServiceId", existingServices.get(0).getIdInsuranceService());
                }
                
                serviceMap.put("imported", isImported);
                
                // Añadir información adicional
                if (serviceNode.has("copay")) serviceMap.put("copay", serviceNode.get("copay").asDouble());
                if (serviceNode.has("pay")) serviceMap.put("pay", serviceNode.get("pay").asDouble());
                if (serviceNode.has("total")) serviceMap.put("total", serviceNode.get("total").asDouble());
                
                // Añadir categorías
                if (serviceNode.has("categories") && serviceNode.get("categories").isArray()) {
                    List<String> categories = new ArrayList<>();
                    for (JsonNode catNode : serviceNode.get("categories")) {
                        categories.add(catNode.asText());
                    }
                    serviceMap.put("categories", categories);
                }
                
                // Añadir subcategorías
                if (serviceNode.has("subcategories") && serviceNode.get("subcategories").isArray()) {
                    List<String> subcategories = new ArrayList<>();
                    for (JsonNode subNode : serviceNode.get("subcategories")) {
                        subcategories.add(subNode.asText());
                    }
                    serviceMap.put("subcategories", subcategories);
                }
                
                responseList.add(serviceMap);
            }
            
            String response = objectMapper.writeValueAsString(responseList);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
    
    private void handleApproveHospitalService(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Cuerpo de la petición para aprobar servicio: " + requestBody);
            
            Map<String, Object> data = objectMapper.readValue(requestBody, new TypeReference<Map<String, Object>>() {});
            
            // Validar datos requeridos
            if (!data.containsKey("hospitalServiceId") || !data.containsKey("categoryId") || 
                !data.containsKey("subcategoryId") || !data.containsKey("coveragePercentage")) {
                sendResponse(exchange, 400, "{\"error\":\"Faltan campos requeridos\"}");
                return;
            }
            
            String hospitalServiceId = (String) data.get("hospitalServiceId");
            Long categoryId = Long.valueOf(data.get("categoryId").toString());
            Long subcategoryId = Long.valueOf(data.get("subcategoryId").toString());
            Integer coveragePercentage = Integer.valueOf(data.get("coveragePercentage").toString());
            
            System.out.println("Procesando aprobación para servicio del hospital con ID: " + hospitalServiceId);
            
            // Obtener detalles del servicio del hospital - asegurarnos que la URL termine con /
            String serviceUrl = HOSPITAL_API_BASE_URL + "/services/" + hospitalServiceId + "/";
            System.out.println("URL para obtener servicio: " + serviceUrl);
            String serviceJson = HttpClientUtil.get(serviceUrl);
            
            if (serviceJson == null) {
                System.err.println("No se pudo obtener información del servicio con ID: " + hospitalServiceId);
                sendResponse(exchange, 500, "{\"error\":\"No se pudo obtener el detalle del servicio del hospital\"}");
                return;
            }
            
            // Procesar la respuesta JSON
            System.out.println("Respuesta del hospital: " + serviceJson);
            JsonNode serviceNode;
            try {
                serviceNode = objectMapper.readTree(serviceJson);
            } catch (Exception e) {
                System.err.println("Error al procesar JSON del servicio: " + serviceJson);
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"Error al procesar la respuesta del hospital\"}");
                return;
            }
            
            // Crear o actualizar el servicio en nuestra base de datos
            InsuranceService service = null;
            List<InsuranceService> existingServices = insuranceServiceDAO.findByExternalId(hospitalServiceId);
            
            if (existingServices != null && !existingServices.isEmpty()) {
                // Actualizar servicio existente
                service = existingServices.get(0);
                System.out.println("Actualizando servicio existente con ID: " + service.getIdInsuranceService());
            } else {
                // Crear nuevo servicio
                service = new InsuranceService();
                service.setExternalId(hospitalServiceId);
                System.out.println("Creando nuevo servicio");
            }
            
            // Configurar datos del servicio
            service.setName(serviceNode.has("name") ? 
                serviceNode.get("name").asText() : "Servicio del hospital");
            service.setDescription(data.containsKey("description") ? 
                (String) data.get("description") : "Servicio importado del hospital");
            
            // Establecer categoría y subcategoría
            Category category = categoryDAO.findById(categoryId);
            Category subcategory = categoryDAO.findById(subcategoryId);
            
            if (category == null || subcategory == null) {
                sendResponse(exchange, 400, "{\"error\":\"Categoría o subcategoría no encontrada\"}");
                return;
            }
            
            service.setCategory(category);
            service.setSubcategory(subcategory);
            
            // Establecer precio y cobertura
            double price = 0.0;
            if (serviceNode.has("total") && !serviceNode.get("total").isNull()) {
                price = serviceNode.get("total").asDouble();
            } else if (serviceNode.has("price") && !serviceNode.get("price").isNull()) {
                price = serviceNode.get("price").asDouble();
            }
            
            service.setPrice(price);
            service.setCoveragePercentage(coveragePercentage);
            service.setEnabled(1); // Activado por defecto
            
            // Guardar el servicio
            InsuranceService savedService;
            if (service.getIdInsuranceService() != null) {
                savedService = insuranceServiceDAO.update(service);
            } else {
                savedService = insuranceServiceDAO.create(service);
            }
            
            if (savedService != null) {
                System.out.println("Servicio guardado correctamente con ID: " + savedService.getIdInsuranceService());
                String response = objectMapper.writeValueAsString(savedService);
                sendResponse(exchange, 200, response);
            } else {
                System.err.println("Error al guardar el servicio en la base de datos");
                sendResponse(exchange, 500, "{\"error\":\"No se pudo guardar el servicio\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
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
        
        // Si hay un ID externo, guardarlo
        if (data.containsKey("externalId")) {
            service.setExternalId((String) data.get("externalId"));
        }
        
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
        
        if (data.containsKey("externalId")) {
            existingService.setExternalId((String) data.get("externalId"));
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