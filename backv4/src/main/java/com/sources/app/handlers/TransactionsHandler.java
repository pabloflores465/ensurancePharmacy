package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.TransactionsDAO;
import com.sources.app.entities.Transactions;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionsHandler implements HttpHandler {

    private final TransactionsDAO transactionsDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/transactions";

    public TransactionsHandler(TransactionsDAO transactionsDAO) {
        this.transactionsDAO = transactionsDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Configuración de CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Preflight
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Verificar endpoint
        String path = exchange.getRequestURI().getPath();
        if (!path.equalsIgnoreCase(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        switch (exchange.getRequestMethod().toUpperCase()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> params = parseQuery(query);
            if (params.containsKey("user_id")) {
                // Buscar transacciones por ID de usuario (cliente)
                try {
                    Long userId = Long.parseLong(params.get("user_id"));
                    List<Transactions> list = transactionsDAO.findByUserId(userId);
                    String jsonResponse = objectMapper.writeValueAsString(list);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1);
                }
            } else if (params.containsKey("id")) {
                // Buscar transacción por su ID
                try {
                    Long id = Long.parseLong(params.get("id"));
                    Transactions t = transactionsDAO.findById(id);
                    if (t != null) {
                        String jsonResponse = objectMapper.writeValueAsString(t);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBytes);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1);
                }
            } else {
                // Si no se especifica parámetro, retorna todas las transacciones
                List<Transactions> list = transactionsDAO.findAll();
                String jsonResponse = objectMapper.writeValueAsString(list);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        } else {
            // Sin query, retornar todas las transacciones
            List<Transactions> list = transactionsDAO.findAll();
            String jsonResponse = objectMapper.writeValueAsString(list);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Transactions t = objectMapper.readValue(requestBody, Transactions.class);
            if (t.getUser() == null || t.getUser().getIdUser() == null ||
                    t.getHospital() == null || t.getHospital().getIdHospital() == null) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            Transactions created = transactionsDAO.create(
                    t.getUser().getIdUser(),        // Extraer el ID de User
                    t.getHospital().getIdHospital(),  // Extraer el ID de Hospital
                    t.getTransDate(),
                    t.getTotal(),
                    t.getCopay(),
                    t.getTransactionComment(),
                    t.getResult(),
                    t.getCovered(),
                    t.getAuth()
            );
            if (created != null) {
                String jsonResponse = objectMapper.writeValueAsString(created);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(201, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } else {
                exchange.sendResponseHeaders(500, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Transactions t = objectMapper.readValue(requestBody, Transactions.class);
        Transactions updated = transactionsDAO.update(t);
        if (updated != null) {
            String jsonResponse = objectMapper.writeValueAsString(updated);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } else {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        kv -> kv[0],
                        kv -> kv.length > 1 ? kv[1] : ""
                ));
    }
}
