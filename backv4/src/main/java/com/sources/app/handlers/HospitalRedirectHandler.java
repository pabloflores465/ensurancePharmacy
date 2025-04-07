package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.util.HttpClientUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Manejador que reenvía solicitudes de /hospitals a /api/hospital
 * para mantener compatibilidad con el frontend
 */
public class HospitalRedirectHandler implements HttpHandler {
    private static final String ENDPOINT = "/api/hospital-integration";
    private static final String HOSPITAL_API_BASE_URL = "http://localhost:8000/api";
    private final ObjectMapper objectMapper;

    public HospitalRedirectHandler() {
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
        if (!path.startsWith(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        try {
            // Subruta después del endpoint base
            String subPath = path.substring(ENDPOINT.length());
            
            // Si no hay subruta, redirigir al root de la API del hospital
            if (subPath.isEmpty() || subPath.equals("/")) {
                subPath = "/";
            }
            
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            String requestUrl = HOSPITAL_API_BASE_URL + subPath;
            
            // Añadir query string si existe
            if (query != null && !query.isEmpty()) {
                requestUrl += "?" + query;
            }
            
            // Procesar la solicitud según el método HTTP
            String requestBody = null;
            if ("POST".equals(method) || "PUT".equals(method)) {
                requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            }
            
            // Hacer la solicitud al backend del hospital y obtener la respuesta
            String response;
            switch (method) {
                case "GET":
                    response = HttpClientUtil.get(requestUrl);
                    break;
                case "POST":
                    response = HttpClientUtil.post(requestUrl, requestBody);
                    break;
                case "PUT":
                    response = HttpClientUtil.put(requestUrl, requestBody);
                    break;
                case "DELETE":
                    response = HttpClientUtil.delete(requestUrl);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
                    return;
            }
            
            // Enviar la respuesta al cliente
            if (response != null) {
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Error al conectar con el servicio del hospital\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
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