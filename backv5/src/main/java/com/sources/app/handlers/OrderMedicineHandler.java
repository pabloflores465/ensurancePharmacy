package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.OrderMedicineDAO;
import com.sources.app.entities.OrderMedicine;
import com.sources.app.entities.OrderMedicineId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OrderMedicineHandler implements HttpHandler {
    private final OrderMedicineDAO orderMedicineDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/order_medicines";

    public OrderMedicineHandler(OrderMedicineDAO orderMedicineDAO) {
        this.orderMedicineDAO = orderMedicineDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())){
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        if("POST".equalsIgnoreCase(method)){
            // CREATE: se espera JSON con orders (con id), medicine (con id), quantity, cost, total
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                OrderMedicine createOM = objectMapper.readValue(requestBody, OrderMedicine.class);
                OrderMedicine om = orderMedicineDAO.create(
                        createOM.getOrders(),
                        createOM.getMedicine(),
                        createOM.getQuantity(),
                        createOM.getCost(),
                        createOM.getTotal()
                );
                if(om != null){
                    String jsonResponse = objectMapper.writeValueAsString(om);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(201, responseBytes.length);
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
        } else if("GET".equalsIgnoreCase(method)){
            // READ: Si query "id=" se espera formato id=orderId,medicineId; sino, retorna todos
            try {
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")){
                    String[] parts = query.substring(3).split(",");
                    if(parts.length == 2){
                        Long orderId = Long.parseLong(parts[0]);
                        Long medicineId = Long.parseLong(parts[1]);
                        OrderMedicineId id = new OrderMedicineId(orderId, medicineId);
                        OrderMedicine om = orderMedicineDAO.getById(id);
                        if(om != null){
                            String jsonResponse = objectMapper.writeValueAsString(om);
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
                        exchange.sendResponseHeaders(400, -1);
                    }
                } else {
                    List<OrderMedicine> list = orderMedicineDAO.getAll();
                    String jsonResponse = objectMapper.writeValueAsString(list);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try(OutputStream os = exchange.getResponseBody()){
                        os.write(responseBytes);
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else if("PUT".equalsIgnoreCase(method)){
            // UPDATE: se espera JSON completo de OrderMedicine (con clave compuesta)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                OrderMedicine updateOM = objectMapper.readValue(requestBody, OrderMedicine.class);
                OrderMedicine om = orderMedicineDAO.update(updateOM);
                if(om != null){
                    String jsonResponse = objectMapper.writeValueAsString(om);
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
