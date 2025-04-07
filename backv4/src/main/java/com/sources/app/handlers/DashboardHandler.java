package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.ServiceApprovalDAO;
import com.sources.app.dao.HospitalDAO;
import com.sources.app.dao.InsuranceServiceDAO;
import com.sources.app.dao.HospitalInsuranceServiceDAO;
import com.sources.app.entities.ServiceApproval;
import com.sources.app.util.PharmacyClient;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manejador para mostrar un dashboard de integraciones entre sistemas
 */
public class DashboardHandler implements HttpHandler {
    private final ServiceApprovalDAO serviceApprovalDAO;
    private final HospitalDAO hospitalDAO;
    private final InsuranceServiceDAO insuranceServiceDAO;
    private final HospitalInsuranceServiceDAO hospitalInsuranceServiceDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/dashboard";

    public DashboardHandler(
            ServiceApprovalDAO serviceApprovalDAO,
            HospitalDAO hospitalDAO,
            InsuranceServiceDAO insuranceServiceDAO,
            HospitalInsuranceServiceDAO hospitalInsuranceServiceDAO) {
        this.serviceApprovalDAO = serviceApprovalDAO;
        this.hospitalDAO = hospitalDAO;
        this.insuranceServiceDAO = insuranceServiceDAO;
        this.hospitalInsuranceServiceDAO = hospitalInsuranceServiceDAO;
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
        
        if (path.equals(ENDPOINT)) {
            if (method.equals("GET")) {
                handleDashboardGet(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.equals(ENDPOINT + "/status")) {
            if (method.equals("GET")) {
                handleStatusGet(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }
    
    /**
     * Maneja la solicitud GET para el dashboard
     */
    private void handleDashboardGet(HttpExchange exchange) throws IOException {
        try {
            // Recopilar datos para el dashboard
            Map<String, Object> dashboard = new HashMap<>();
            
            // 1. Estadísticas de servicios aprobados
            List<ServiceApproval> approvals = serviceApprovalDAO.findAll();
            int totalApprovals = approvals.size();
            int approvedCount = 0;
            int pendingCount = 0;
            int rejectedCount = 0;
            int completedCount = 0;
            int withPrescription = 0;
            
            double totalCoveredAmount = 0;
            double totalPatientAmount = 0;
            double totalAmount = 0;
            
            // Filtrar por estado
            for (ServiceApproval approval : approvals) {
                if ("APPROVED".equals(approval.getStatus())) {
                    approvedCount++;
                } else if ("PENDING".equals(approval.getStatus())) {
                    pendingCount++;
                } else if ("REJECTED".equals(approval.getStatus())) {
                    rejectedCount++;
                } else if ("COMPLETED".equals(approval.getStatus())) {
                    completedCount++;
                }
                
                if (approval.getPrescriptionId() != null) {
                    withPrescription++;
                }
                
                totalCoveredAmount += approval.getCoveredAmount();
                totalPatientAmount += approval.getPatientAmount();
                totalAmount += approval.getServiceCost();
            }
            
            Map<String, Object> approvalStats = new HashMap<>();
            approvalStats.put("total", totalApprovals);
            approvalStats.put("approved", approvedCount);
            approvalStats.put("pending", pendingCount);
            approvalStats.put("rejected", rejectedCount);
            approvalStats.put("completed", completedCount);
            approvalStats.put("withPrescription", withPrescription);
            approvalStats.put("totalCoveredAmount", totalCoveredAmount);
            approvalStats.put("totalPatientAmount", totalPatientAmount);
            approvalStats.put("totalAmount", totalAmount);
            
            // 2. Hospitales integrados
            int hospitalCount = hospitalDAO.findAll().size();
            int serviceCount = insuranceServiceDAO.findAll().size();
            
            // 3. Recopilar información sobre recetas de farmacia
            // Esta información se obtendría de la farmacia, pero como es una simulación, usaremos datos ficticios
            Map<String, Object> pharmacyStats = new HashMap<>();
            pharmacyStats.put("totalPrescriptions", withPrescription);
            pharmacyStats.put("dispensedPrescriptions", withPrescription / 2);
            pharmacyStats.put("pendingPrescriptions", withPrescription - (withPrescription / 2));
            
            // 4. Generar últimas transacciones
            List<Map<String, Object>> recentTransactions = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            int transactionsToShow = Math.min(10, approvals.size());
            for (int i = 0; i < transactionsToShow; i++) {
                ServiceApproval approval = approvals.get(i);
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", approval.getId());
                transaction.put("approvalCode", approval.getApprovalCode());
                transaction.put("serviceName", approval.getServiceName());
                transaction.put("status", approval.getStatus());
                transaction.put("date", approval.getApprovalDate() != null ? 
                    dateFormat.format(approval.getApprovalDate()) : "N/A");
                transaction.put("coveredAmount", approval.getCoveredAmount());
                transaction.put("patientAmount", approval.getPatientAmount());
                transaction.put("total", approval.getServiceCost());
                transaction.put("hasPrescription", approval.getPrescriptionId() != null);
                
                recentTransactions.add(transaction);
            }
            
            // 5. Estadísticas de conexión entre sistemas
            Map<String, Object> connections = new HashMap<>();
            connections.put("hospitalIntegrations", hospitalCount);
            connections.put("pharmacyIntegrations", 1); // Suponemos que hay una farmacia integrada
            connections.put("serviceCategories", 5); // Valor ficticio
            connections.put("serviceSubcategories", 15); // Valor ficticio
            
            // Armar el objeto dashboard final
            dashboard.put("approvalStats", approvalStats);
            dashboard.put("hospitalCount", hospitalCount);
            dashboard.put("serviceCount", serviceCount);
            dashboard.put("pharmacyStats", pharmacyStats);
            dashboard.put("recentTransactions", recentTransactions);
            dashboard.put("connections", connections);
            dashboard.put("lastUpdated", dateFormat.format(new Date()));
            
            // Enviar respuesta
            sendResponse(exchange, 200, objectMapper.writeValueAsString(dashboard));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Error al generar el dashboard: " + e.getMessage()
            )));
        }
    }
    
    /**
     * Maneja la solicitud GET para el estado de integración
     */
    private void handleStatusGet(HttpExchange exchange) throws IOException {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 1. Comprobar conexión con hospital
            boolean hospitalConnection = false;
            try {
                // Esto sería una llamada real al hospital
                hospitalConnection = true;
            } catch (Exception e) {
                // La conexión falló
            }
            
            // 2. Comprobar conexión con farmacia
            boolean pharmacyConnection = false;
            try {
                // Esto sería una llamada real a la farmacia
                // String response = PharmacyClient.get("/status");
                pharmacyConnection = true;
            } catch (Exception e) {
                // La conexión falló
            }
            
            status.put("hospitalConnection", hospitalConnection);
            status.put("pharmacyConnection", pharmacyConnection);
            status.put("databaseConnection", true); // Asumimos que si llegamos aquí, la base de datos está conectada
            status.put("timestamp", System.currentTimeMillis());
            
            sendResponse(exchange, 200, objectMapper.writeValueAsString(status));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, objectMapper.writeValueAsString(Map.of(
                "success", false,
                "message", "Error al verificar el estado: " + e.getMessage()
            )));
        }
    }
    
    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);
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