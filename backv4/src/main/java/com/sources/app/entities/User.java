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

    @Column(name = "ROL", nullable = false)
    private String role;  // Rol del usuario

    // Relación ManyToOne obligatoria con Policy
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_POLICY", nullable = false)
    private Policy policy;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;  // Estado (habilitado/deshabilitado)

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    // Constructor sin argumentos
    public User() {}

    // Constructor completo
    public User(String name, Long cui, String phone, String email, String address, Date birthDate, String role, Policy policy, Integer enabled, String password) {
        this.name = name;
        this.cui = cui;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.birthDate = birthDate;
        this.role = role;
        this.policy = policy;
        this.enabled = enabled;
        this.password = password;
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

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
