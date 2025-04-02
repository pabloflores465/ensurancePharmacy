package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.HospitalInsuranceServiceDAO;
import com.sources.app.dao.HospitalDAO;
import com.sources.app.dao.InsuranceServiceDAO;
import com.sources.app.entities.HospitalInsuranceService;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.InsuranceService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HospitalInsuranceServiceHandler implements HttpHandler {
    private final HospitalInsuranceServiceDAO hospitalInsuranceServiceDAO;
    private final HospitalDAO hospitalDAO;
    private final InsuranceServiceDAO insuranceServiceDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/hospital-services";

    public HospitalInsuranceServiceHandler(
            HospitalInsuranceServiceDAO hospitalInsuranceServiceDAO,
            HospitalDAO hospitalDAO,
            InsuranceServiceDAO insuranceServiceDAO) {
        this.hospitalInsuranceServiceDAO = hospitalInsuranceServiceDAO;
        this.hospitalDAO = hospitalDAO;
        this.insuranceServiceDAO = insuranceServiceDAO;
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
            // GET para listar relaciones o filtrar por hospital o servicio
            if ("GET".equals(exchange.getRequestMethod())) {
                if (path.equals(ENDPOINT)) {
                    handleGetByFilters(exchange, query);
                } else {
                    // Obtener por ID
                    String idStr = path.substring(ENDPOINT.length() + 1);
                    try {
                        Long id = Long.parseLong(idStr);
                        handleGetById(exchange, id);
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(400, -1);
                    }
                }
            } 
            // POST para aprobar un servicio para un hospital
            else if ("POST".equals(exchange.getRequestMethod()) && path.equals(ENDPOINT + "/approve")) {
                handleApprove(exchange);
            } 
            // POST para revocar aprobación
            else if ("POST".equals(exchange.getRequestMethod()) && path.equals(ENDPOINT + "/revoke")) {
                handleRevoke(exchange);
            } 
            // DELETE para eliminar una relación
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

    private void handleGetByFilters(HttpExchange exchange, String query) throws IOException {
        List<HospitalInsuranceService> relations = null;
        
        if (query != null && query.startsWith("hospital=")) {
            try {
                Long hospitalId = Long.parseLong(query.substring("hospital=".length()));
                Hospital hospital = hospitalDAO.findById(hospitalId);
                if (hospital != null) {
                    relations = hospitalInsuranceServiceDAO.findApprovedByHospital(hospital);
                }
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
        } else if (query != null && query.startsWith("service=")) {
            try {
                Long serviceId = Long.parseLong(query.substring("service=".length()));
                InsuranceService service = insuranceServiceDAO.findById(serviceId);
                if (service != null) {
                    relations = hospitalInsuranceServiceDAO.findHospitalsByService(service);
                }
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
        }
        
        if (relations != null) {
            String response = objectMapper.writeValueAsString(relations);
            sendResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleGetById(HttpExchange exchange, Long id) throws IOException {
        HospitalInsuranceService relation = hospitalInsuranceServiceDAO.findById(id);
        
        if (relation != null) {
            String response = objectMapper.writeValueAsString(relation);
            sendResponse(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleApprove(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        Long hospitalId = Long.valueOf(data.get("hospitalId").toString());
        Long serviceId = Long.valueOf(data.get("serviceId").toString());
        String notes = (String) data.get("notes");
        
        Hospital hospital = hospitalDAO.findById(hospitalId);
        InsuranceService service = insuranceServiceDAO.findById(serviceId);
        
        if (hospital == null || service == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        
        HospitalInsuranceService relation = hospitalInsuranceServiceDAO.approveService(hospital, service, notes);
        
        if (relation != null) {
            String response = objectMapper.writeValueAsString(relation);
            sendResponse(exchange, 201, response);
        } else {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleRevoke(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        Long hospitalId = Long.valueOf(data.get("hospitalId").toString());
        Long serviceId = Long.valueOf(data.get("serviceId").toString());
        
        Hospital hospital = hospitalDAO.findById(hospitalId);
        InsuranceService service = insuranceServiceDAO.findById(serviceId);
        
        if (hospital == null || service == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        
        boolean success = hospitalInsuranceServiceDAO.revokeApproval(hospital, service);
        
        if (success) {
            sendResponse(exchange, 200, "{\"success\":true}");
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleDelete(HttpExchange exchange, Long id) throws IOException {
        boolean success = hospitalInsuranceServiceDAO.delete(id);
        
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