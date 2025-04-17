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

    // Relación ManyToOne opcional con Policy (ya no es obligatoria)
    @ManyToOne(optional = true)
    @JoinColumn(name = "ID_POLICY", nullable = true)
    private Policy policy;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;  // Estado (habilitado/deshabilitado)

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "PAID_SERVICE")
    private Boolean paidService;  // Ahora puede ser nulo

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate;  // Fecha de expiración del servicio

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT")
    private Date createdAt;  // Fecha de creación del usuario

    // Constructor sin argumentos
    public User() {
        this.createdAt = new Date(); // Inicializar con la fecha actual al crear un usuario
        this.paidService = null; // Inicialmente no definido
    }

    // Constructor completo
    public User(String name, Long cui, String phone, String email, String address, Date birthDate, String role, Policy policy, Integer enabled, String password, Boolean paidService, Date expirationDate) {
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
        this.paidService = paidService; // Puede ser null
        this.expirationDate = expirationDate;
        this.createdAt = new Date();
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

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Boolean getPaidService() { return paidService; }
    public void setPaidService(Boolean paidService) { this.paidService = paidService; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }
}
