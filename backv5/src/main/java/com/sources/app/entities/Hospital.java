package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "HOSPITAL")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HOSPITAL")
    private Long idHospital;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "ENABLED")
    private Character enabled; // Usamos Character para mapear CHAR(1)

    // Constructor vacío
    public Hospital() {
    }

    // Constructor con campos
    public Hospital(String name, String phone, String email, String address, Character enabled) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.enabled = enabled;
    }

    // Getters y Setters
    public Long getIdHospital() {
        return idHospital;
    }

    public void setIdHospital(Long idHospital) {
        this.idHospital = idHospital;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Character getEnabled() {
        return enabled;
    }

    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }
}
