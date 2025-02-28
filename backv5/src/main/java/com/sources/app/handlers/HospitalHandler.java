package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Hospital;
import com.sources.app.dao.HospitalDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HospitalHandler implements HttpHandler {
    private final HospitalDAO hospitalDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/hospitals";

    public HospitalHandler(HospitalDAO hospitalDAO) {
        this.hospitalDAO = hospitalDAO;
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
            // CREATE: se espera JSON con name, phone, email, address, enabled
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Hospital createHospital = objectMapper.readValue(requestBody, Hospital.class);
                Hospital hospital = hospitalDAO.create(
                        createHospital.getName(),
                        createHospital.getPhone(),
                        createHospital.getEmail(),
                        createHospital.getAddress(),
                        createHospital.getEnabled()
                );
                if(hospital != null){
                    String jsonResponse = objectMapper.writeValueAsString(hospital);
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
                    Hospital hospital = hospitalDAO.getById(id);
                    if(hospital != null){
                        String jsonResponse = objectMapper.writeValueAsString(hospital);
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
                    List<Hospital> list = hospitalDAO.getAll();
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
            // UPDATE: se espera JSON completo de Hospital
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Hospital updateHospital = objectMapper.readValue(requestBody, Hospital.class);
                Hospital hospital = hospitalDAO.update(updateHospital);
                if(hospital != null){
                    String jsonResponse = objectMapper.writeValueAsString(hospital);
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
