package com.sources.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class NotificationHandler implements HttpHandler {
    private final ObjectMapper objectMapper;
    private static final String ENDPOINT = "/api/notifications/email";
    private static final Logger LOGGER = Logger.getLogger(NotificationHandler.class.getName());

    // Propiedades de correo
    private final Properties mailProperties;
    private final String senderEmail;
    private final String senderPassword;

    public NotificationHandler() {
        this.objectMapper = new ObjectMapper();
        this.mailProperties = loadMailProperties();
        this.senderEmail = mailProperties.getProperty("mail.sender.email");
        this.senderPassword = mailProperties.getProperty("mail.sender.password");
        
        LOGGER.info("NotificationHandler inicializado con email: " + this.senderEmail);
    }


    private Properties loadMailProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) {
                LOGGER.severe("No se pudo encontrar el archivo mail.properties");
                throw new IOException("No se pudo encontrar el archivo mail.properties");
            }
            props.load(input);
            LOGGER.info("Propiedades de correo cargadas correctamente");
        } catch (IOException e) {
            LOGGER.severe("Error al cargar las propiedades de correo: " + e.getMessage());
            e.printStackTrace();
        }
        return props;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Configuración de CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Manejo de preflight
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Verificar que la ruta sea la correcta
        String path = exchange.getRequestURI().getPath();
        if (!path.equalsIgnoreCase(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // Solo permitimos el método POST
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // Leer el cuerpo de la petición
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> emailRequest = objectMapper.readValue(requestBody, Map.class);

                // Extraer datos del email
                String to = emailRequest.get("to");
                String subject = emailRequest.get("subject");
                String body = emailRequest.get("body");

                // Validar datos
                if (to == null || subject == null || body == null) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                // Enviar el email
                boolean emailSent = sendEmail(to, subject, body);

                if (emailSent) {
                    // Email enviado con éxito
                    Map<String, Object> response = Map.of(
                        "success", true,
                        "message", "Email enviado con éxito"
                    );
                    
                    String jsonResponse = objectMapper.writeValueAsString(response);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    // Error al enviar el email
                    exchange.sendResponseHeaders(500, -1);
                }
            } catch (Exception e) {
                LOGGER.severe("Error al procesar la solicitud: " + e.getMessage());
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        } else {
            // Método no permitido
            exchange.sendResponseHeaders(405, -1);
        }
    }


    private boolean sendEmail(String to, String subject, String body) {
        try {
            LOGGER.info("Enviando email a: " + to);
            
            // Configurar propiedades para el servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", mailProperties.getProperty("mail.smtp.auth"));
            props.put("mail.smtp.starttls.enable", mailProperties.getProperty("mail.smtp.starttls.enable"));
            props.put("mail.smtp.host", mailProperties.getProperty("mail.smtp.host"));
            props.put("mail.smtp.port", mailProperties.getProperty("mail.smtp.port"));
            
            // Crear sesión con autenticación
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });
            
            // Para depuración
            session.setDebug(true);
            
            // Crear el mensaje
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            // Enviar el mensaje
            Transport.send(message);
            
            LOGGER.info("Email enviado con éxito a: " + to);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 