package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.OrdersDAO;
import com.sources.app.entities.Orders;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OrdersHandler implements HttpHandler {
    private final OrdersDAO ordersDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/orders";

    public OrdersHandler(OrdersDAO ordersDAO) {
        this.ordersDAO = ordersDAO;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Encabezados CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())){
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        if("POST".equalsIgnoreCase(method)){
            // CREATE: se espera JSON con status
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Orders createOrder = objectMapper.readValue(requestBody, Orders.class);
                Orders order = ordersDAO.create(createOrder.getStatus(), createOrder.getUser().getIdUser());
                if(order != null) {
                    String jsonResponse = objectMapper.writeValueAsString(order);
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
            // READ: si query "id=" se retorna un Order; si no, todos
            try {
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")) {
                    Long id = Long.parseLong(query.substring(3));
                    Orders order = ordersDAO.getById(id);
                    if(order != null) {
                        String jsonResponse = objectMapper.writeValueAsString(order);
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
                    List<Orders> list = ordersDAO.getAll();
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
            // UPDATE: se espera JSON completo de Orders (con id)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Orders updateOrder = objectMapper.readValue(requestBody, Orders.class);
                Orders order = ordersDAO.update(updateOrder);
                if(order != null){
                    String jsonResponse = objectMapper.writeValueAsString(order);
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
