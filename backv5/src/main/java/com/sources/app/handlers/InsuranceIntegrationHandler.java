package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.PrescriptionDAO;
import com.sources.app.dao.BillDAO;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.Bill;
import com.sources.app.util.ExternalServiceClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Manejador para integrar la farmacia con el seguro
 */
public class InsuranceIntegrationHandler implements HttpHandler {
    private final PrescriptionDAO prescriptionDAO;
    private final BillDAO billDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/insurance";
    private static final String INSURANCE_API_BASE_URL = "http://localhost:8080/api/pharmacy-insurance";
    private static final ExternalServiceClient externalServiceClient = new ExternalServiceClient();

    public InsuranceIntegrationHandler(PrescriptionDAO prescriptionDAO, BillDAO billDAO) {
        this.prescriptionDAO = prescriptionDAO;
        this.billDAO = billDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.equals(ENDPOINT + "/validate-prescription")) {
            if (method.equals("POST")) {
                handleValidatePrescription(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.equals(ENDPOINT + "/check-coverage")) {
            if (method.equals("POST")) {
                handleCheckCoverage(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    /**
     * Maneja la validación de una receta con el seguro
     */
    private void handleValidatePrescription(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        // Validar datos requeridos
        if (!data.containsKey("prescriptionId") || !data.containsKey("approvalCode")) {
            sendBadRequest(exchange, "Campos requeridos: prescriptionId, approvalCode");
            return;
        }
        
        Long prescriptionId = Long.valueOf(data.get("prescriptionId").toString());
        String approvalCode = (String) data.get("approvalCode");
        
        // Buscar la receta
        Prescription prescription = prescriptionDAO.findById(prescriptionId);
        
        if (prescription == null) {
            sendNotFound(exchange, "Receta no encontrada");
            return;
        }
        
        // Si ya existe una factura para esta receta, retornar error
        Bill existingBill = billDAO.findByPrescriptionId(prescriptionId);
        if (existingBill != null) {
            sendResponse(exchange, 400, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Esta receta ya fue procesada y facturada"
            )));
            return;
        }
        
        // Calcular el total de la receta
        double totalAmount = prescription.calculateTotal();
        
        // Datos para enviar al seguro
        Map<String, Object> insuranceData = new HashMap<>();
        insuranceData.put("approvalCode", approvalCode);
        insuranceData.put("prescriptionId", prescriptionId);
        insuranceData.put("totalAmount", totalAmount);
        
        try {
            // Llamar al servicio del seguro
            String serviceType = "INSURANCE";
            String endpoint = "/validate-prescription";
            String jsonResponse = externalServiceClient.post(serviceType, endpoint, insuranceData);
            
            // Parsear la respuesta
            Map<String, Object> insuranceResponse = objectMapper.readValue(jsonResponse, Map.class);
            
            if (Boolean.TRUE.equals(insuranceResponse.get("success"))) {
                // Crear la factura
                Bill bill = new Bill();
                bill.setPrescription(prescription);
                bill.setTotalAmount(totalAmount);
                bill.setCoveredAmount((Double) insuranceResponse.get("coveredAmount"));
                bill.setPatientAmount((Double) insuranceResponse.get("patientAmount"));
                bill.setInsuranceApprovalCode((String) insuranceResponse.get("approvalCode"));
                bill.setCreatedAt(new Date());
                bill.setStatus("PENDING");
                
                Bill savedBill = billDAO.create(bill);
                
                if (savedBill != null) {
                    Map<String, Object> response = new HashMap<>(insuranceResponse);
                    response.put("billId", savedBill.getId());
                    sendResponse(exchange, 200, objectMapper.writeValueAsString(response));
                } else {
                    sendResponse(exchange, 500, objectMapper.writeValueAsString(Map.of(
                        "success", false,
                        "message", "Error al guardar la factura"
                    )));
                }
            } else {
                // Enviar la respuesta de error del seguro
                sendResponse(exchange, 400, jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Error al comunicarse con el seguro: " + e.getMessage()
            )));
        }
    }
    
    /**
     * Verifica la cobertura de una receta
     */
    private void handleCheckCoverage(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        // Validar datos requeridos
        if (!data.containsKey("prescriptionId")) {
            sendBadRequest(exchange, "Campo requerido: prescriptionId");
            return;
        }
        
        Long prescriptionId = Long.valueOf(data.get("prescriptionId").toString());
        
        try {
            // Llamar al servicio del seguro
            String serviceType = "INSURANCE";
            String endpoint = "/check-coverage/" + prescriptionId;
            String jsonResponse = externalServiceClient.get(serviceType, endpoint);
            
            // Enviar la respuesta del seguro
            sendResponse(exchange, 200, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Error al comunicarse con el seguro: " + e.getMessage()
            )));
        }
    }
    
    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);
    }
    
    private void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 400, objectMapper.writeValueAsString(Map.of(
            "success", false,
            "message", message
        )));
    }
    
    private void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 404, objectMapper.writeValueAsString(Map.of(
            "success", false,
            "message", message
        )));
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