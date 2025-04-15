package com.sources.app.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import com.sources.app.dao.VerificationDAO;

public class VerificationHandler implements HttpHandler {
    private final VerificationDAO verificationDAO;
    private static final String ENDPOINT = "/api2/verification";
    
    public VerificationHandler() {
        this.verificationDAO = new VerificationDAO();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Encabezados CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        if (!path.startsWith(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String email = null;
            String query = exchange.getRequestURI().getQuery();
            
            if (query != null && query.contains("email=")) {
                email = query.split("email=")[1];
                if (email.contains("&")) {
                    email = email.split("&")[0];
                }
            }
            
            if (email == null || email.isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            
            // Log para depuración
            System.out.println("Email recibido para verificación: " + email);
            
            boolean isVerified = verificationDAO.verifyUser(email);
            System.out.println("Resultado de verificación: " + isVerified);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            String jsonResponse = isVerified ? "1" : "0";
            byte[] responseBytes = jsonResponse.getBytes();
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.getResponseBody().close();
            exchange.close();
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    }
}
