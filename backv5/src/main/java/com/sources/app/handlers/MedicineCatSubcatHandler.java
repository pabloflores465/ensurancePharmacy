package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.MedicineCatSubcatDAO;
import com.sources.app.entities.MedicineCatSubcat;
import com.sources.app.entities.MedicineCatSubcatId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MedicineCatSubcatHandler implements HttpHandler {
    private final MedicineCatSubcatDAO mcsDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/medicine_catsubcats";

    public MedicineCatSubcatHandler(MedicineCatSubcatDAO mcsDAO) {
        this.mcsDAO = mcsDAO;
        this.objectMapper = new ObjectMapper();
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

        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method)) {
            // CREATE: Se espera JSON con Medicine, Category y Subcategory (al menos con sus id)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                MedicineCatSubcat createObj = objectMapper.readValue(requestBody, MedicineCatSubcat.class);
                MedicineCatSubcat mcs = mcsDAO.create(
                        createObj.getMedicine(),
                        createObj.getCategory(),
                        createObj.getSubcategory()
                );
                if (mcs != null) {
                    String jsonResponse = objectMapper.writeValueAsString(mcs);
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
        } else if ("GET".equalsIgnoreCase(method)) {
            // READ: Si se pasa un query "id=" con formato id=medicineId,categoryId,subcategoryId; sino, retorna todos.
            try {
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")) {
                    String[] parts = query.substring(3).split(",");
                    if(parts.length == 3) {
                        Long medId = Long.parseLong(parts[0]);
                        Long catId = Long.parseLong(parts[1]);
                        Long subcatId = Long.parseLong(parts[2]);
                        MedicineCatSubcatId id = new MedicineCatSubcatId(medId, catId, subcatId);
                        MedicineCatSubcat mcs = mcsDAO.getById(id);
                        if(mcs != null) {
                            String jsonResponse = objectMapper.writeValueAsString(mcs);
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
                    List<MedicineCatSubcat> list = mcsDAO.getAll();
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
        } else if ("PUT".equalsIgnoreCase(method)) {
            // UPDATE: Se espera JSON completo de MedicineCatSubcat (incluyendo clave compuesta)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                MedicineCatSubcat updateObj = objectMapper.readValue(requestBody, MedicineCatSubcat.class);
                MedicineCatSubcat mcs = mcsDAO.update(updateObj);
                if(mcs != null) {
                    String jsonResponse = objectMapper.writeValueAsString(mcs);
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
