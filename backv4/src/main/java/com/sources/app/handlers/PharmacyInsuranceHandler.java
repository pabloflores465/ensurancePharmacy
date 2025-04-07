package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.ServiceApprovalDAO;
import com.sources.app.entities.ServiceApproval;
import com.sources.app.util.PharmacyClient;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador para la integración entre el seguro y la farmacia
 */
public class PharmacyInsuranceHandler implements HttpHandler {
    private final ServiceApprovalDAO serviceApprovalDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/pharmacy-insurance";

    public PharmacyInsuranceHandler(ServiceApprovalDAO serviceApprovalDAO) {
        this.serviceApprovalDAO = serviceApprovalDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers for all requests
        HibernateUtil.setCorsHeaders(exchange);
        
        // Handle OPTIONS requests (CORS preflight)
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        
        if (path.equals(ENDPOINT + "/validate-prescription")) {
            if (method.equals("POST")) {
                handleValidatePrescription(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches(ENDPOINT + "/check-coverage/\\d+")) {
            if (method.equals("GET")) {
                long prescriptionId = Long.parseLong(path.substring((ENDPOINT + "/check-coverage/").length()));
                handleCheckCoverage(exchange, prescriptionId);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }
    
    /**
     * Maneja la validación de una receta médica con el seguro
     */
    private void handleValidatePrescription(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);
        
        // Validar datos requeridos
        if (!data.containsKey("approvalCode") || !data.containsKey("prescriptionId") || !data.containsKey("totalAmount")) {
            sendBadRequest(exchange, "Se requieren los campos: approvalCode, prescriptionId, totalAmount");
            return;
        }
        
        String approvalCode = (String) data.get("approvalCode");
        Long prescriptionId = Long.valueOf(data.get("prescriptionId").toString());
        Double totalAmount = Double.valueOf(data.get("totalAmount").toString());
        
        // Buscar la aprobación de servicio por su código
        ServiceApproval approval = serviceApprovalDAO.findByApprovalCode(approvalCode);
        
        if (approval == null) {
            sendNotFound(exchange, "Código de aprobación no encontrado");
            return;
        }
        
        // Verificar el estado de la aprobación
        if (!"APPROVED".equals(approval.getStatus())) {
            sendResponse(exchange, 400, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "El servicio no está en estado aprobado"
            )));
            return;
        }
        
        // Verificar si ya tiene una receta asociada
        if (approval.getPrescriptionId() != null) {
            sendResponse(exchange, 400, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Este servicio ya tiene una receta asociada"
            )));
            return;
        }
        
        // Actualizar la aprobación con la información de la receta
        approval.setPrescriptionId(prescriptionId);
        approval.setPrescriptionTotal(totalAmount);
        
        // Calcular cuánto cubre el seguro (por ejemplo, 70% del total)
        double coveragePercentage = 0.7; // 70% de cobertura
        double coveredAmount = totalAmount * coveragePercentage;
        double patientAmount = totalAmount - coveredAmount;
        
        approval.setCoveredAmount(approval.getCoveredAmount() + coveredAmount);
        approval.setPatientAmount(approval.getPatientAmount() + patientAmount);
        
        // Guardar los cambios
        ServiceApproval updated = serviceApprovalDAO.update(approval);
        
        if (updated != null) {
            // Preparar respuesta para la farmacia
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("approvalCode", updated.getApprovalCode());
            response.put("prescriptionId", updated.getPrescriptionId());
            response.put("coveredAmount", coveredAmount);
            response.put("patientAmount", patientAmount);
            response.put("totalAmount", totalAmount);
            
            sendResponse(exchange, 200, objectMapper.writeValueAsString(response));
        } else {
            sendResponse(exchange, 500, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Error al actualizar la aprobación del servicio"
            )));
        }
    }
    
    /**
     * Maneja la consulta de cobertura para una receta específica
     */
    private void handleCheckCoverage(HttpExchange exchange, long prescriptionId) throws IOException {
        // Buscar la aprobación de servicio por el ID de receta
        ServiceApproval approval = serviceApprovalDAO.findByPrescriptionId(prescriptionId);
        
        if (approval == null) {
            sendNotFound(exchange, "No se encontró aprobación para esta receta");
            return;
        }
        
        // Preparar respuesta con la información de cobertura
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("approvalCode", approval.getApprovalCode());
        response.put("prescriptionId", approval.getPrescriptionId());
        response.put("coveredAmount", approval.getCoveredAmount());
        response.put("patientAmount", approval.getPatientAmount());
        response.put("totalAmount", approval.getPrescriptionTotal());
        response.put("status", approval.getStatus());
        
        sendResponse(exchange, 200, objectMapper.writeValueAsString(response));
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
        // CORS headers already set in handle method
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
} 