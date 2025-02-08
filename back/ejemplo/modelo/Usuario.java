package com.ejemplo.modelo;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Usuario {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String hireDate;
    private double salary;

    public Usuario() {}

    public Usuario(int id, String firstName, String lastName, String email, String hireDate, double salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}