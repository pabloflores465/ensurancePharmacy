package com.ejemplo.test;

import com.ejemplo.util.HibernateUtil;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConexionDB {
    public static void main(String[] args) {
        // Abrimos una sesión con Hibernate utilizando try-with-resources para que se cierre automáticamente
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Obtenemos la conexión JDBC desde la sesión
            Connection connection = session.doReturningWork(conn -> conn);

            // Ejecutamos una consulta sencilla (en Oracle se puede usar "SELECT 1 FROM DUAL")
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT 1 FROM DUAL");

            // Procesamos el resultado
            if (rs.next()) {
                int resultado = rs.getInt(1);
                System.out.println("Conexión exitosa. Resultado de la consulta: " + resultado);
            } else {
                System.out.println("No se obtuvo resultado de la consulta.");
            }

            // Cerramos el ResultSet y Statement
            rs.close();
            statement.close();

        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos:");
            e.printStackTrace();
        } finally {
            // Cerramos la SessionFactory si ya no se utilizará más
            HibernateUtil.shutdown();
        }
    }
}