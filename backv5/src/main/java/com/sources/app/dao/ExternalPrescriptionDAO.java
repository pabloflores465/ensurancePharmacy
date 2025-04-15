package com.sources.app.dao;

import com.sources.app.entities.Prescription;
import com.sources.app.util.HibernateUtil;
import com.sun.net.httpserver.HttpExchange;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExternalPrescriptionDAO {
    public Prescription getbyId(Long id, String email, HttpExchange exchange) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            String path = exchange.getRequestURI().getPath();
            try {
                // Create a URL object with the API endpoint including the provided email
                URL url = new URL(path + "/api2/verification?email=" + email);
                // Open a connection to the URL
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                // Set the HTTP request method to GET
                con.setRequestMethod("GET");
                // Get the response code
                int status = con.getResponseCode();
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                // Disconnect the connection
                con.disconnect();
                // Save the response into the variable 'verify'
                String verify = content.toString();
                
                // Si el usuario no está verificado, retornar null
                if (!"1".equals(verify)) {
                    return null;
                }
                
            } catch(Exception e) {
                e.printStackTrace();
                transaction.rollback();
                return null;
            }
            
            Prescription prescription = session.get(Prescription.class, id);
            transaction.commit();
            return prescription;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}