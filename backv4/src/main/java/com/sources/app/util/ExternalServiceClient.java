package com.sources.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cliente para comunicación con servicios externos (hospitales y farmacias)
 */
public class ExternalServiceClient {
    
    private static final String HOSPITAL_BASE_URL = "http://localhost:8000/api";
    private static final String PHARMACY_BASE_URL = "http://localhost:8080/api";
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Realiza una petición GET a la API externa
     */
    public String get(String endpoint, boolean isHospital) throws Exception {
        String baseUrl = isHospital ? HOSPITAL_BASE_URL : PHARMACY_BASE_URL;
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
        } else {
            throw new Exception("Error en la petición GET: " + responseCode);
        }
    }
    
    /**
     * Realiza una petición POST a la API externa
     */
    public String post(String endpoint, Object requestBody, boolean isHospital) throws Exception {
        String baseUrl = isHospital ? HOSPITAL_BASE_URL : PHARMACY_BASE_URL;
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String jsonInputString = objectMapper.writeValueAsString(requestBody);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            throw new Exception("Error en la petición POST: " + responseCode + ", Respuesta: " + response.toString());
        }
    }
    
    /**
     * Realiza una petición PUT a la API externa
     */
    public String put(String endpoint, Object requestBody, boolean isHospital) throws Exception {
        String baseUrl = isHospital ? HOSPITAL_BASE_URL : PHARMACY_BASE_URL;
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String jsonInputString = objectMapper.writeValueAsString(requestBody);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
        } else {
            throw new Exception("Error en la petición PUT: " + responseCode);
        }
    }
    
    /**
     * Métodos asíncronos
     */
    public CompletableFuture<String> getAsync(String endpoint, boolean isHospital) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return get(endpoint, isHospital);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    public CompletableFuture<String> postAsync(String endpoint, Object requestBody, boolean isHospital) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return post(endpoint, requestBody, isHospital);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    public CompletableFuture<String> putAsync(String endpoint, Object requestBody, boolean isHospital) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return put(endpoint, requestBody, isHospital);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
} 