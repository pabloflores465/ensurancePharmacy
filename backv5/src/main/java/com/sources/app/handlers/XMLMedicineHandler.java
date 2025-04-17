package com.sources.app.handlers;

import com.sources.app.dao.MedicineDAO;
import com.sources.app.entities.Medicine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XMLMedicineHandler implements HttpHandler {
    private final MedicineDAO medicineDAO;
    private static final String ENDPOINT = "/api2/medicines-xml";

    public XMLMedicineHandler(MedicineDAO medicineDAO) {
        this.medicineDAO = medicineDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!path.startsWith(ENDPOINT)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        // Get all medicines from the database
        List<Medicine> medicines = medicineDAO.getAll();
        
        // Convert the list of medicines to XML format
        String xmlResponse = convertToXml(medicines);
        
        // Set response headers
        exchange.getResponseHeaders().set("Content-Type", "application/xml");
        byte[] responseBytes = xmlResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        
        // Write the response
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String convertToXml(List<Medicine> medicines) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<medicines>\n");
        
        for (Medicine medicine : medicines) {
            xml.append("  <medicine>\n");
            xml.append("    <idMedicine>").append(medicine.getIdMedicine()).append("</idMedicine>\n");
            xml.append("    <name>").append(escapeXml(medicine.getName())).append("</name>\n");
            xml.append("    <activeMedicament>").append(escapeXml(medicine.getActiveMedicament())).append("</activeMedicament>\n");
            xml.append("    <description>").append(escapeXml(medicine.getDescription())).append("</description>\n");
            xml.append("    <image>").append(escapeXml(medicine.getImage())).append("</image>\n");
            xml.append("    <concentration>").append(escapeXml(medicine.getConcentration())).append("</concentration>\n");
            xml.append("    <presentacion>").append(medicine.getPresentacion()).append("</presentacion>\n");
            xml.append("    <stock>").append(medicine.getStock()).append("</stock>\n");
            xml.append("    <brand>").append(escapeXml(medicine.getBrand())).append("</brand>\n");
            xml.append("    <prescription>").append(medicine.getPrescription()).append("</prescription>\n");
            xml.append("    <price>").append(medicine.getPrice()).append("</price>\n");
            xml.append("    <soldUnits>").append(medicine.getSoldUnits()).append("</soldUnits>\n");
            xml.append("  </medicine>\n");
        }
        
        xml.append("</medicines>");
        return xml.toString();
    }
    
    private String escapeXml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
} 