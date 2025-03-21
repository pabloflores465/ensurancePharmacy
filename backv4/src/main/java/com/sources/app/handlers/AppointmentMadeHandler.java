package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sources.app.entities.AppointmentMade;
import com.sources.app.dao.AppointmentMadeDAO;
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

public class AppointmentMadeHandler implements HttpHandler {

    private final AppointmentMadeDAO appointmentMadeDAO;
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/appointmentmade";

    public AppointmentMadeHandler(AppointmentMadeDAO appointmentMadeDAO) {
        this.appointmentMadeDAO = appointmentMadeDAO;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!path.equalsIgnoreCase(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        switch (exchange.getRequestMethod().toUpperCase()){
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
        if(query != null && query.contains("id=")){
            Map<String,String> params = parseQuery(query);
            try{
                Long id = Long.parseLong(params.get("id"));
                AppointmentMade am = appointmentMadeDAO.findById(id);
                if(am != null){
                    String jsonResponse = objectMapper.writeValueAsString(am);
                    exchange.getResponseHeaders().set("Content-Type","application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    try(OutputStream os = exchange.getResponseBody()){
                        os.write(responseBytes);
                    }
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            } catch(NumberFormatException e){
                exchange.sendResponseHeaders(400, -1);
            }
        } else {
            List<AppointmentMade> list = appointmentMadeDAO.findAll();
            String jsonResponse = objectMapper.writeValueAsString(list);
            exchange.getResponseHeaders().set("Content-Type","application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try(OutputStream os = exchange.getResponseBody()){
                os.write(responseBytes);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            AppointmentMade am = objectMapper.readValue(requestBody, AppointmentMade.class);
            AppointmentMade created = appointmentMadeDAO.create(
                    am.getIdCita(),
                    am.getIdUser(),
                    am.getAppointmentMadeDate()
            );
            if(created != null){
                String jsonResponse = objectMapper.writeValueAsString(created);
                exchange.getResponseHeaders().set("Content-Type","application/json");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(201, responseBytes.length);
                try(OutputStream os = exchange.getResponseBody()){
                    os.write(responseBytes);
                }
            } else {
                exchange.sendResponseHeaders(500,-1);
            }
        } catch(Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500,-1);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        AppointmentMade am = objectMapper.readValue(requestBody, AppointmentMade.class);
        AppointmentMade updated = appointmentMadeDAO.update(am);
        if(updated != null){
            String jsonResponse = objectMapper.writeValueAsString(updated);
            exchange.getResponseHeaders().set("Content-Type","application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try(OutputStream os = exchange.getResponseBody()){
                os.write(responseBytes);
            }
        } else {
            exchange.sendResponseHeaders(500,-1);
        }
    }



    private Map<String,String> parseQuery(String query){
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        kv -> kv[0],
                        kv -> kv.length > 1 ? kv[1] : ""
                ));
    }
}
