package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USER")
    private Long idUser;  // Clave primaria

    @Column(name = "NAME", nullable = false)
    private String name;  // Nombre del usuario

    @Column(name = "CUI", nullable = false)
    private Long cui;  // CUI (identificación única)

    @Column(name = "PHONE")
    private String phone;  // Teléfono

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;  // Correo electrónico

    @Column(name = "ADDRESS")
    private String address;  // Dirección

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDATE")
    private Date birthDate;  // Fecha de nacimiento

    @Column(name = "ROL")
    private String role;  // Rol del usuario

    @Column(name = "ID_POLICY")
    private Long idPolicy;  // ID de política

    @Column(name = "ENABLED")
    private Integer enabled;  // Estado (habilitado/deshabilitado)

    // Constructores, getters y setters
    public User() {}

    public User(String name, Long cui, String phone, String email, String address, Date birthDate, String role, Long idPolicy, Integer enabled) {
        this.name = name;
        this.cui = cui;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.birthDate = birthDate;
        this.role = role;
        this.idPolicy = idPolicy;
        this.enabled = enabled;
    }

    // Getters y Setters
    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCui() { return cui; }
    public void setCui(Long cui) { this.cui = cui; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getIdPolicy() { return idPolicy; }
    public void setIdPolicy(Long idPolicy) { this.idPolicy = idPolicy; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
