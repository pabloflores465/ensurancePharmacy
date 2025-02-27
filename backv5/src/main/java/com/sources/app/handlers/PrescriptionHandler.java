package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.Prescription;
import com.sources.app.entities.Hospital;
import com.sources.app.entities.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PrescriptionHandler implements HttpHandler {
    private final PrescriptionDAO prescriptionDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/prescriptions";

    public PrescriptionHandler(PrescriptionDAO prescriptionDAO) {
        this.prescriptionDAO = prescriptionDAO;
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
            // CREATE: se espera JSON con hospital (con id), user (con id) y approved
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Prescription createPres = objectMapper.readValue(requestBody, Prescription.class);
                Hospital hospital = createPres.getHospital();
                User user = createPres.getUser();
                Prescription prescription = prescriptionDAO.create(hospital, user, createPres.getApproved());
                if(prescription != null){
                    String jsonResponse = objectMapper.writeValueAsString(prescription);
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
            // READ: si ?id= se retorna un Prescription; si no, todos
            try {
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")){
                    Long id = Long.parseLong(query.substring(3));
                    Prescription prescription = prescriptionDAO.getById(id);
                    if(prescription != null){
                        String jsonResponse = objectMapper.writeValueAsString(prescription);
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
                    List<Prescription> list = prescriptionDAO.getAll();
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
            // UPDATE: se espera JSON completo de Prescription (con id)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Prescription updatePres = objectMapper.readValue(requestBody, Prescription.class);
                Prescription prescription = prescriptionDAO.update(updatePres);
                if(prescription != null){
                    String jsonResponse = objectMapper.writeValueAsString(prescription);
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
