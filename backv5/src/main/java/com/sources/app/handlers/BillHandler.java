package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.BillDAO;
import com.sources.app.entities.Bill;
import com.sources.app.entities.Prescription;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BillHandler implements HttpHandler {
    private final BillDAO billDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/bills";

    public BillHandler(BillDAO billDAO) {
        this.billDAO = billDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method)) {
            // CREATE: se espera JSON con prescription (al menos con id), taxes, subtotal, copay, total
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Bill createBill = objectMapper.readValue(requestBody, Bill.class);
                Prescription prescription = createBill.getPrescription();
                Bill bill = billDAO.create(
                        prescription,
                        createBill.getTaxes(),
                        createBill.getSubtotal(),
                        createBill.getCopay(),
                        createBill.getTotal()
                );
                if (bill != null) {
                    String jsonResponse = objectMapper.writeValueAsString(bill);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(201, responseBytes.length);
                    try (OutputStream os = exchange.getResponseBody()){
                        os.write(responseBytes);
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
            } catch(Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else if ("GET".equalsIgnoreCase(method)) {
            // READ: si ?id= se retorna por id, sino se retornan todos
            try {
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")) {
                    Long id = Long.parseLong(query.substring(3));
                    Bill bill = billDAO.getById(id);
                    if(bill != null) {
                        String jsonResponse = objectMapper.writeValueAsString(bill);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        try(OutputStream os = exchange.getResponseBody()){
                            os.write(responseBytes);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } else {
                    List<Bill> bills = billDAO.getAll();
                    String jsonResponse = objectMapper.writeValueAsString(bills);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try(OutputStream os = exchange.getResponseBody()){
                        os.write(responseBytes);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else if ("PUT".equalsIgnoreCase(method)) {
            // UPDATE: se espera JSON completo del Bill (con id)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Bill updateBill = objectMapper.readValue(requestBody, Bill.class);
                Bill bill = billDAO.update(updateBill);
                if(bill != null) {
                    String jsonResponse = objectMapper.writeValueAsString(bill);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try(OutputStream os = exchange.getResponseBody()){
                        os.write(responseBytes);
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1);
                }
            } catch(Exception e){
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
