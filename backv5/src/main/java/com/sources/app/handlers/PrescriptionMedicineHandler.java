package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.PrescriptionMedicineDAO;
import com.sources.app.entities.PrescriptionMedicine;
import com.sources.app.entities.PrescriptionMedicineId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PrescriptionMedicineHandler implements HttpHandler {
    private final PrescriptionMedicineDAO prescriptionMedicineDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/prescription_medicines";

    public PrescriptionMedicineHandler(PrescriptionMedicineDAO prescriptionMedicineDAO) {
        this.prescriptionMedicineDAO = prescriptionMedicineDAO;
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
            // CREATE: se espera JSON con prescription (con id), medicine (con id), dosis, frecuencia y duracion
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                PrescriptionMedicine createPM = objectMapper.readValue(requestBody, PrescriptionMedicine.class);
                PrescriptionMedicine pm = prescriptionMedicineDAO.create(
                        createPM.getPrescription(),
                        createPM.getMedicine(),
                        createPM.getDosis(),
                        createPM.getFrecuencia(),
                        createPM.getDuracion()
                );
                if(pm != null){
                    String jsonResponse = objectMapper.writeValueAsString(pm);
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
            // READ: si query "id=" se espera formato id=prescriptionId,medicineId; sino, retorna todos
            try {
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")){
                    String[] parts = query.substring(3).split(",");
                    if(parts.length == 2){
                        Long presId = Long.parseLong(parts[0]);
                        Long medId = Long.parseLong(parts[1]);
                        PrescriptionMedicineId id = new PrescriptionMedicineId(presId, medId);
                        PrescriptionMedicine pm = prescriptionMedicineDAO.getById(id);
                        if(pm != null){
                            String jsonResponse = objectMapper.writeValueAsString(pm);
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
                    List<PrescriptionMedicine> list = prescriptionMedicineDAO.getAll();
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
            // UPDATE: se espera JSON completo de PrescriptionMedicine (con clave compuesta)
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                PrescriptionMedicine updatePM = objectMapper.readValue(requestBody, PrescriptionMedicine.class);
                PrescriptionMedicine pm = prescriptionMedicineDAO.update(updatePM);
                if(pm != null){
                    String jsonResponse = objectMapper.writeValueAsString(pm);
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
