package com.sources.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente para realizar llamadas a la API de Farmacia
 */
public class PharmacyClient {

    private static final String PHARMACY_API_BASE_URL = "http://localhost:8082/api2";
    private static final int TIMEOUT = 10000; // 10 segundos
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Punto de extensión para tests: abre una conexión HTTP para la URL dada.
     * Los tests pueden mockear este método estático para inyectar un
     * HttpURLConnection.
     */
    public static HttpURLConnection open(String urlString) throws IOException {
        URL url = new URL(urlString);
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Realiza una petición GET a la API de farmacia
     *
     * @param endpoint Endpoint de la API (comenzando con /)
     * @return Respuesta en formato JSON
     * @throws IOException Si ocurre un error en la comunicación
     */
    public static String get(String endpoint) throws IOException {
        HttpURLConnection connection = open(PHARMACY_API_BASE_URL + endpoint);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        try {
            return handleResponse(connection);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Realiza una petición POST a la API de farmacia
     *
     * @param endpoint Endpoint de la API (comenzando con /)
     * @param data Datos a enviar en el cuerpo de la petición
     * @return Respuesta en formato JSON
     * @throws IOException Si ocurre un error en la comunicación
     */
    public static String post(String endpoint, Object data) throws IOException {
        HttpURLConnection connection = open(PHARMACY_API_BASE_URL + endpoint);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoOutput(true);
        String jsonData = objectMapper.writeValueAsString(data);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try {
            return handleResponse(connection);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Realiza una petición PUT a la API de farmacia
     *
     * @param endpoint Endpoint de la API (comenzando con /)
     * @param data Datos a enviar en el cuerpo de la petición
     * @return Respuesta en formato JSON
     * @throws IOException Si ocurre un error en la comunicación
     */
    public static String put(String endpoint, Object data) throws IOException {
        HttpURLConnection connection = open(PHARMACY_API_BASE_URL + endpoint);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoOutput(true);
        String jsonData = objectMapper.writeValueAsString(data);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        try {
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                // Leer error stream para facilitar debug y evitar stubs innecesarios en tests
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        error.append(line.trim());
                    }
                    throw new IOException("Error en la petición PUT: " + responseCode + ", Respuesta: " + error);
                } catch (Exception ex) {
                    throw new IOException("Error en la petición PUT: " + responseCode);
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Procesa la respuesta HTTP y la convierte a String
     */
    private static String handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();

        if (responseCode >= 200 && responseCode < 300) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String errorLine;
                while ((errorLine = br.readLine()) != null) {
                    error.append(errorLine.trim());
                }
                return "{\"error\":true,\"message\":\"" + error.toString() + "\",\"statusCode\":" + responseCode + "}";
            }
        }
    }
}
