package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "\"User\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USER")
    private Long idUser;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CUI")
    private String cui;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ADDRESS")
    private String address;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDATE")
    private Date birthDate;

    @Column(name = "ROL")
    private String role;

    @Column(name = "ENABLED")
    private Character enabled;

    @Column(name = "PASSWORD")
    private String password;

    // Constructor vacío (requerido por JPA)
    public User() {
    }

    // Constructor con campos (ejemplo)
    public User(String name, String cui, String phone, String email, Date birthDate, String address, String password) {
        this.name = name;
        this.cui = cui;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.password = password;
        this.role = "usuario";
        this.enabled = '1';  // Usamos '1' como Character
    }

    // Getters y Setters
    public Long getIdUser() {
        return idUser;
    }
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCui() {
        return cui;
    }
    public void setCui(String cui) {
        this.cui = cui;
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
    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public Character getEnabled() {
        return enabled;
    }
    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
