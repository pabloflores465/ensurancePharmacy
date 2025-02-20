package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "PHARMACY")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PHARMACY")
    private Long idPharmacy;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "ADDRESS", nullable = false, length = 255)
    private String address;

    @Column(name = "PHONE", nullable = false)
    private Long phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Pharmacy() {}

    // Getters y Setters
    public Long getIdPharmacy() { return idPharmacy; }
    public void setIdPharmacy(Long idPharmacy) { this.idPharmacy = idPharmacy; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Long getPhone() { return phone; }
    public void setPhone(Long phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
