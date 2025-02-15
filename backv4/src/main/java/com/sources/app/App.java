package com.sources.app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

public class App {

    // Definición de la entidad Employee mapeada a la tabla LOLA
    @Entity(name = "Employee")
    @Table(name = "LOLA")
    public static class Employee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "EMPLOYEEID")
        private Long employeeId;
        
        @Column(name = "FIRSTNAME")
        private String firstName;
        
        @Column(name = "LASTNAME")
        private String lastName;
        
        @Column(name = "EMAIL")
        private String email;
        
        @Temporal(TemporalType.DATE)
        @Column(name = "HIREDATE")
        private Date hireDate;
        
        @Column(name = "SALARY")
        private Double salary;
        
        // Constructor vacío requerido por Hibernate
        public Employee() {}
        
        public Employee(String firstName, String lastName, String email, Date hireDate, Double salary) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.hireDate = hireDate;
            this.salary = salary;
        }
        
        // Getters y setters
        public Long getEmployeeId() {
            return employeeId;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public Date getHireDate() {
            return hireDate;
        }
        public void setHireDate(Date hireDate) {
            this.hireDate = hireDate;
        }
        public Double getSalary() {
            return salary;
        }
        public void setSalary(Double salary) {
            this.salary = salary;
        }
        
        @Override
        public String toString() {
            return "Employee{" +
                    "employeeId=" + employeeId +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", hireDate=" + hireDate +
                    ", salary=" + salary +
                    '}';
        }
    }

    // Método para construir el SessionFactory de Hibernate
    public static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    // Se crea un SessionFactory estático para reutilizarlo en cada petición
    private static final SessionFactory sessionFactory = buildSessionFactory();

    public static void main(String[] args) throws Exception {
        // Crear e iniciar el servidor HTTP en el puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        // Se crea el contexto /employees que gestionará las peticiones a este endpoint
        server.createContext("/employees", new EmployeesHandler());
        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("Servidor iniciado en http://localhost:8080/employees");
    }

    // Handler para gestionar las peticiones GET al endpoint /employees
    static class EmployeesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Se valida que el método sea GET
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
                return;
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Employee> employees;
            Session session = null;
            try {
                // Abrir una sesión de Hibernate y ejecutar la consulta
                session = sessionFactory.openSession();
                session.beginTransaction();
                employees = session.createQuery("from Employee", Employee.class).getResultList();
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
                return;
            } finally {
                if (session != null) {
                    session.close();
                }
            }
            
            // Convertir la lista de empleados a JSON
            String jsonResponse = objectMapper.writeValueAsString(employees);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}