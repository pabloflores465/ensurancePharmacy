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
    private static final String HOSPITAL_API_BASE_URL = "http://0.0.0.0:5050/api";

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
            System.out.println("Intentando obtener servicios del hospital...");
            
            // Lista de posibles URLs a intentar
            String[] possibleUrls = {
                "http://0.0.0.0:5050/api/services/",
                "http://0.0.0.0:5050/api/services",
                "http://localhost:5050/api/services/",
                "http://localhost:5050/api/services",
                "http://127.0.0.1:5050/api/services/",
                "http://127.0.0.1:5050/api/services",
                "http://192.168.0.4:5052/api/services/",
                "http://192.168.0.4:5052/api/services",
                "http://192.168.0.4:5050/api/services/",
                "http://192.168.0.4:5050/api/services"
            };
            
            // Obtener servicios del hospital intentando todas las URLs
            String servicesJson = null;
            String successUrl = "";
            
            for (String url : possibleUrls) {
                System.out.println("Intentando obtener servicios desde: " + url);
                servicesJson = HttpClientUtil.get(url);
                if (servicesJson != null) {
                    System.out.println("Éxito con URL: " + url);
                    successUrl = url;
                    break;
                }
            }
            
            if (servicesJson == null) {
                System.err.println("No se pudieron obtener los servicios del hospital con ninguna URL.");
                sendResponse(exchange, 503, "{\"error\":\"No se pudieron obtener los servicios del hospital. Servicio no disponible.\",\"details\":\"Se intentaron múltiples URLs sin éxito.\"}");
                return;
            }
            
            // Almacenar la URL exitosa para futuros usos
            System.out.println("URL exitosa para futuras referencias: " + successUrl);
            
            // Procesar la respuesta JSON
            JsonNode servicesNode;
            try {
                servicesNode = objectMapper.readTree(servicesJson);
            } catch (Exception e) {
                System.err.println("Error al parsear la respuesta JSON: " + e.getMessage());
                System.err.println("Contenido de la respuesta: " + servicesJson);
                sendResponse(exchange, 500, "{\"error\":\"Formato de respuesta del hospital inválido\"}");
                return;
            }
            
            JsonNode servicesArray = servicesNode.has("services") ? servicesNode.get("services") : servicesNode;
            
            if (servicesArray == null || !servicesArray.isArray()) {
                System.err.println("La respuesta no contiene un array de servicios. Contenido: " + servicesJson);
                // Si el nodo principal es un array, usarlo directamente
                if (servicesNode.isArray()) {
                    servicesArray = servicesNode;
                } else {
                    sendResponse(exchange, 500, "{\"error\":\"Formato de respuesta del hospital inválido - no se encontró array de servicios\"}");
                    return;
                }
            }
            
            List<Map<String, Object>> responseList = new ArrayList<>();
            
            // Procesar cada servicio
            for (JsonNode serviceNode : servicesArray) {
                try {
                    Map<String, Object> serviceMap = new HashMap<>();
                    
                    // Extraer ID de servicio, manejando diferentes formatos
                    String serviceId;
                    if (serviceNode.has("_id")) {
                        serviceId = serviceNode.get("_id").asText();
                    } else if (serviceNode.has("id")) {
                        serviceId = serviceNode.get("id").asText();
                    } else {
                        System.err.println("Servicio sin ID, saltando: " + serviceNode);
                        continue;
                    }
                    
                    serviceMap.put("hospitalServiceId", serviceId);
                    
                    // Extraer nombre del servicio con fallback
                    if (serviceNode.has("name")) {
                        serviceMap.put("name", serviceNode.get("name").asText());
                    } else if (serviceNode.has("nombre")) {
                        serviceMap.put("name", serviceNode.get("nombre").asText());
                    } else {
                        serviceMap.put("name", "Servicio " + serviceId);
                    }
                    
                    // Verificar si el servicio ya está importado
                    boolean isImported = false;
                    List<InsuranceService> existingServices = insuranceServiceDAO.findByExternalId(serviceId);
                    if (existingServices != null && !existingServices.isEmpty()) {
                        isImported = true;
                        serviceMap.put("insuranceServiceId", existingServices.get(0).getIdInsuranceService());
                    }
                    
                    serviceMap.put("imported", isImported);
                    
                    // Añadir información adicional con manejo seguro de tipos
                    addSafeFieldToMap(serviceMap, serviceNode, "copay", "copay");
                    addSafeFieldToMap(serviceMap, serviceNode, "pay", "pay");
                    addSafeFieldToMap(serviceMap, serviceNode, "total", "total");
                    addSafeFieldToMap(serviceMap, serviceNode, "price", "price");
                    addSafeFieldToMap(serviceMap, serviceNode, "cost", "cost");
                    
                    // Añadir categorías si existen
                    if (serviceNode.has("categories") && serviceNode.get("categories").isArray()) {
                        List<String> categories = new ArrayList<>();
                        for (JsonNode catNode : serviceNode.get("categories")) {
                            categories.add(catNode.asText());
                        }
                        serviceMap.put("categories", categories);
                    }
                    
                    // Añadir subcategorías si existen
                    if (serviceNode.has("subcategories") && serviceNode.get("subcategories").isArray()) {
                        List<String> subcategories = new ArrayList<>();
                        for (JsonNode subNode : serviceNode.get("subcategories")) {
                            subcategories.add(subNode.asText());
                        }
                        serviceMap.put("subcategories", subcategories);
                    }
                    
                    responseList.add(serviceMap);
                } catch (Exception e) {
                    System.err.println("Error al procesar servicio: " + e.getMessage());
                    // Continuar con el siguiente servicio
                }
            }
            
            String response = objectMapper.writeValueAsString(responseList);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener servicios del hospital: " + e.getMessage());
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
            
            // Obtener detalles del servicio del hospital
            String serviceJson = null;
            
            // Probar varias variantes de URL para obtener detalles del servicio
            String[] urlVariants = {
                "http://0.0.0.0:5050/api/services/" + hospitalServiceId + "/",
                "http://0.0.0.0:5050/api/services/" + hospitalServiceId,
                "http://0.0.0.0:5050/api/service/" + hospitalServiceId + "/",
                "http://0.0.0.0:5050/api/service/" + hospitalServiceId,
                "http://localhost:5050/api/services/" + hospitalServiceId + "/",
                "http://localhost:5050/api/services/" + hospitalServiceId,
                "http://127.0.0.1:5050/api/services/" + hospitalServiceId + "/",
                "http://127.0.0.1:5050/api/services/" + hospitalServiceId,
                "http://192.168.0.4:5052/api/services/" + hospitalServiceId + "/",
                "http://192.168.0.4:5052/api/services/" + hospitalServiceId
            };
            
            for (String serviceUrl : urlVariants) {
                System.out.println("Intentando obtener servicio desde: " + serviceUrl);
                serviceJson = HttpClientUtil.get(serviceUrl);
                if (serviceJson != null) {
                    System.out.println("Éxito con URL: " + serviceUrl);
                    break;
                }
            }
            
            InsuranceService service = null;
            
            // Si no pudimos obtener detalles del servicio, continuar con los datos que ya tenemos
            if (serviceJson == null) {
                System.err.println("No se pudo obtener información detallada del servicio con ID: " + hospitalServiceId);
                System.out.println("Continuando con datos básicos proporcionados en la solicitud...");
                
                // Buscar si el servicio ya existe
                List<InsuranceService> existingServices = insuranceServiceDAO.findByExternalId(hospitalServiceId);
                
                if (existingServices != null && !existingServices.isEmpty()) {
                    // Actualizar servicio existente
                    service = existingServices.get(0);
                    System.out.println("Actualizando servicio existente con ID: " + service.getIdInsuranceService());
                } else {
                    // Crear nuevo servicio
                    service = new InsuranceService();
                    service.setExternalId(hospitalServiceId);
                    System.out.println("Creando nuevo servicio con datos básicos");
                }
                
                // Configurar datos básicos del servicio
                String serviceName = data.containsKey("name") ? (String) data.get("name") : "Servicio del hospital " + hospitalServiceId;
                service.setName(serviceName);
                service.setDescription(data.containsKey("description") ? 
                    (String) data.get("description") : "Servicio importado del hospital");
                
                // Establecer precio desde los datos proporcionados si está disponible
                if (data.containsKey("price")) {
                    try {
                        double price = Double.parseDouble(data.get("price").toString());
                        service.setPrice(price);
                    } catch (Exception e) {
                        service.setPrice(0.0); // Valor por defecto
                    }
                } else if (data.containsKey("total")) {
                    try {
                        double price = Double.parseDouble(data.get("total").toString());
                        service.setPrice(price);
                    } catch (Exception e) {
                        service.setPrice(0.0); // Valor por defecto
                    }
                } else {
                    service.setPrice(0.0); // Valor por defecto
                }
            } else {
                // Si tenemos datos del servicio, procesarlos
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
                List<InsuranceService> existingServices = insuranceServiceDAO.findByExternalId(hospitalServiceId);
                
                if (existingServices != null && !existingServices.isEmpty()) {
                    // Actualizar servicio existente
                    service = existingServices.get(0);
                    System.out.println("Actualizando servicio existente con ID: " + service.getIdInsuranceService());
                } else {
                    // Crear nuevo servicio
                    service = new InsuranceService();
                    service.setExternalId(hospitalServiceId);
                    System.out.println("Creando nuevo servicio con datos detallados");
                }
                
                // Configurar datos del servicio según el formato de respuesta
                if (serviceNode.has("name")) {
                    service.setName(serviceNode.get("name").asText());
                } else if (serviceNode.has("nombre")) {
                    service.setName(serviceNode.get("nombre").asText());
                } else {
                    service.setName("Servicio del hospital " + hospitalServiceId);
                }
                
                service.setDescription(data.containsKey("description") ? 
                    (String) data.get("description") : "Servicio importado del hospital");
                
                // Establecer precio
                double price = 0.0;
                if (serviceNode.has("total") && !serviceNode.get("total").isNull()) {
                    try {
                        price = serviceNode.get("total").asDouble();
                    } catch (Exception e) {
                        try {
                            price = Double.parseDouble(serviceNode.get("total").asText());
                        } catch (Exception ex) {
                            // Si falla, continuar con 0
                        }
                    }
                } else if (serviceNode.has("price") && !serviceNode.get("price").isNull()) {
                    try {
                        price = serviceNode.get("price").asDouble();
                    } catch (Exception e) {
                        try {
                            price = Double.parseDouble(serviceNode.get("price").asText());
                        } catch (Exception ex) {
                            // Si falla, continuar con 0
                        }
                    }
                } else if (serviceNode.has("cost") && !serviceNode.get("cost").isNull()) {
                    try {
                        price = serviceNode.get("cost").asDouble();
                    } catch (Exception e) {
                        try {
                            price = Double.parseDouble(serviceNode.get("cost").asText());
                        } catch (Exception ex) {
                            // Si falla, continuar con 0
                        }
                    }
                }
                
                service.setPrice(price);
            }
            
            // Establecer categoría y subcategoría
            Category category = categoryDAO.findById(categoryId);
            Category subcategory = categoryDAO.findById(subcategoryId);
            
            if (category == null || subcategory == null) {
                sendResponse(exchange, 400, "{\"error\":\"Categoría o subcategoría no encontrada\"}");
                return;
            }
            
            service.setCategory(category);
            service.setSubcategory(subcategory);
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

    private void addSafeFieldToMap(Map<String, Object> map, JsonNode node, String jsonField, String mapKey) {
        try {
            if (node.has(jsonField) && !node.get(jsonField).isNull()) {
                if (node.get(jsonField).isNumber()) {
                    map.put(mapKey, node.get(jsonField).asDouble());
                } else {
                    String value = node.get(jsonField).asText();
                    try {
                        // Intentar convertir a número si es posible
                        map.put(mapKey, Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        // Si no es un número, guardar como string
                        map.put(mapKey, value);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar campo " + jsonField + ": " + e.getMessage());
        }
    }
} 