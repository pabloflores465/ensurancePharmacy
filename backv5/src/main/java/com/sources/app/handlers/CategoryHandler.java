package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.CategoryDAO;
import com.sources.app.entities.Category;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CategoryHandler implements HttpHandler {
    private final CategoryDAO categoryDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/categories";

    public CategoryHandler(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
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
            // CREATE: se espera JSON con name
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Category createCat = objectMapper.readValue(requestBody, Category.class);
                Category category = categoryDAO.create(createCat.getName());
                if(category != null){
                    String jsonResponse = objectMapper.writeValueAsString(category);
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
            // READ: si ?id= se obtiene por id, sino todos
            try{
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")){
                    Long id = Long.parseLong(query.substring(3));
                    Category category = categoryDAO.getById(id);
                    if(category != null){
                        String jsonResponse = objectMapper.writeValueAsString(category);
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
                    List<Category> list = categoryDAO.getAll();
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
            // UPDATE: se espera JSON completo de Category
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Category updateCat = objectMapper.readValue(requestBody, Category.class);
                Category category = categoryDAO.update(updateCat);
                if(category != null){
                    String jsonResponse = objectMapper.writeValueAsString(category);
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
