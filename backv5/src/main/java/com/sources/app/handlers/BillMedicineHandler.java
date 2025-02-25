package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.dao.BillMedicineDAO;
import com.sources.app.entities.BillMedicine;
import com.sources.app.entities.Bill;
import com.sources.app.entities.Medicine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BillMedicineHandler implements HttpHandler {
    private final BillMedicineDAO billMedicineDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api2/bill_medicines";

    public BillMedicineHandler(BillMedicineDAO billMedicineDAO) {
        this.billMedicineDAO = billMedicineDAO;
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
            // CREATE: se espera JSON con bill (con id), medicine (con id), quantity, cost, copay, total
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                BillMedicine createBM = objectMapper.readValue(requestBody, BillMedicine.class);
                Bill bill = createBM.getBill();
                Medicine medicine = createBM.getMedicine();
                BillMedicine bm = billMedicineDAO.create(
                        bill,
                        medicine,
                        createBM.getQuantity(),
                        createBM.getCost(),
                        createBM.getCopay(),
                        createBM.getTotal()
                );
                if(bm != null){
                    String jsonResponse = objectMapper.writeValueAsString(bm);
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
            try{
                String query = exchange.getRequestURI().getQuery();
                if(query != null && query.startsWith("id=")){
                    // Para recursos con clave compuesta, se espera un formato id=billId,medicineId
                    String[] parts = query.substring(3).split(",");
                    if(parts.length == 2){
                        // Se asume que se envían ambos id's
                        Long billId = Long.parseLong(parts[0]);
                        Long medicineId = Long.parseLong(parts[1]);
                        // Se crea un objeto clave (BillMedicineId) de forma manual
                        // Suponiendo que tienes un constructor o setters en la clase embebida
                        com.sources.app.entities.BillMedicineId id =
                                new com.sources.app.entities.BillMedicineId(billId, medicineId);
                        BillMedicine bm = billMedicineDAO.getById(id);
                        if(bm != null){
                            String jsonResponse = objectMapper.writeValueAsString(bm);
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
                    List<BillMedicine> list = billMedicineDAO.getAll();
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
            // UPDATE: se espera JSON completo del BillMedicine (con clave compuesta y demás datos)
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                BillMedicine updateBM = objectMapper.readValue(requestBody, BillMedicine.class);
                BillMedicine bm = billMedicineDAO.update(updateBM);
                if(bm != null){
                    String jsonResponse = objectMapper.writeValueAsString(bm);
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
