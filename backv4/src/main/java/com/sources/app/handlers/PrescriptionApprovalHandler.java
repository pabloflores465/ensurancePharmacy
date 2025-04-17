package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.ConfigurableAmountDAO;
import com.sources.app.dao.PrescriptionApprovalDAO;
import com.sources.app.dao.UserDAO;
import com.sources.app.entities.ConfigurableAmount;
import com.sources.app.entities.PrescriptionApproval;
import com.sources.app.entities.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PrescriptionApprovalHandler implements HttpHandler {

    private final PrescriptionApprovalDAO approvalDAO;
    private final UserDAO userDAO;
    private final ConfigurableAmountDAO configDAO;
    private final ObjectMapper objectMapper;

    public PrescriptionApprovalHandler(PrescriptionApprovalDAO approvalDAO, UserDAO userDAO, ConfigurableAmountDAO configDAO) {
        this.approvalDAO = approvalDAO;
        this.userDAO = userDAO;
        this.configDAO = configDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Configurar CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No content for OPTIONS
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.equals("/api/prescriptions/approve") && method.equalsIgnoreCase("POST")) {
                handlePrescriptionApprovalRequest(exchange);
            } else if (path.equals("/api/prescriptions/approvals") && method.equalsIgnoreCase("GET")) {
                handleGetApprovals(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1); // Not Found
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }

    private void handleGetApprovals(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        List<PrescriptionApproval> approvals;
        
        if (query != null && query.contains("userId=")) {
             Map<String, String> params = parseQuery(query);
             try {
                 Long userId = Long.parseLong(params.get("userId"));
                 approvals = approvalDAO.findByUserId(userId);
             } catch (NumberFormatException e) {
                 exchange.sendResponseHeaders(400, -1); // Bad Request
                 return;
             }
        } else {
            approvals = approvalDAO.findAll();
        }

        String jsonResponse = objectMapper.writeValueAsString(approvals);
        sendJsonResponse(exchange, 200, jsonResponse);
    }

    private void handlePrescriptionApprovalRequest(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Map<String, Object> data = objectMapper.readValue(requestBody, Map.class);

        Long userId = Long.valueOf(data.get("userId").toString());
        Double prescriptionCost = Double.valueOf(data.get("totalCost").toString());
        String prescriptionIdHospital = (String) data.get("prescriptionIdHospital");
        String details = (String) data.get("details"); // Asumimos que los detalles vienen como JSON o texto

        User user = userDAO.findById(userId);
        ConfigurableAmount config = configDAO.findCurrentConfig();
        BigDecimal minimumAmount = config.getPrescriptionAmount();

        PrescriptionApproval approval = new PrescriptionApproval();
        approval.setIdUser(userId);
        approval.setPrescriptionIdHospital(prescriptionIdHospital);
        approval.setPrescriptionDetails(details);
        approval.setPrescriptionCost(prescriptionCost);

        // 1. Verificar si el usuario existe
        if (user == null) {
            rejectApproval(approval, "User not found");
            sendJsonResponse(exchange, 404, "{\"error\": \"User not found\", \"status\": \"Rejected\"}");
            return;
        }

        // 2. Verificar si el usuario tiene servicio pagado
        if (user.getPaidService() == null || !user.getPaidService()) {
            rejectApproval(approval, "Client coverage inactive");
            sendJsonResponse(exchange, 400, "{\"error\": \"Client coverage inactive\", \"status\": \"Rejected\"}");
            return;
        }

        // 3. Verificar si el costo es mayor o igual al mínimo configurado
        if (BigDecimal.valueOf(prescriptionCost).compareTo(minimumAmount) < 0) {
            String reason = String.format("Prescription cost (Q%.2f) below minimum threshold (Q%.2f)", 
                                        prescriptionCost, minimumAmount);
            rejectApproval(approval, reason);
            sendJsonResponse(exchange, 400, 
                String.format("{\"error\": \"%s\", \"status\": \"Rejected\"}", reason));
            return;
        }

        // 4. Aprobar la receta
        approvePrescription(approval);
        sendJsonResponse(exchange, 200, 
            String.format("{\"authorizationNumber\": \"%s\", \"status\": \"Approved\"}", 
                          approval.getAuthorizationNumber()));
    }

    private void rejectApproval(PrescriptionApproval approval, String reason) {
        approval.setStatus("REJECTED");
        approval.setRejectionReason(reason);
        approval.setAuthorizationNumber("N/A-" + UUID.randomUUID().toString().substring(0, 8)); // Generar un ID único para rechazos
        approvalDAO.save(approval);
    }

    private void approvePrescription(PrescriptionApproval approval) {
        approval.setStatus("APPROVED");
        approval.setRejectionReason(null);
        approval.setAuthorizationNumber(generateAuthorizationNumber());
        approvalDAO.save(approval);
    }

    private String generateAuthorizationNumber() {
        // Genera un número de autorización único (ejemplo simple)
        return "AUTH-" + UUID.randomUUID().toString().toUpperCase().substring(0, 12);
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
     private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param.length > 1 ? param[1] : ""
                ));
    }
} 