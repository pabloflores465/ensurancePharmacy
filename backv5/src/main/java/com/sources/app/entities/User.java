package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "\"USERS\"")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDATE")
    private Date birthDate;

    @Column(name = "ROL")
    private String role;

    @Column(name = "ENABLED")
    private Character enabled;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne
    @JoinColumn(name = "ID_POLICY", referencedColumnName = "ID_POLICY")
    private Policy policy;
    
    @Column(name = "STATUS")
    private String status;

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
    
    public Policy getPolicy() {
        return policy;
    }
    
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
