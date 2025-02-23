package com.sources.app.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "MEDICINE")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICINE")
    private Long idMedicine;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Relación ManyToOne con Pharmacy (ID_PHARMACY es llave foránea)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PHARMACY", nullable = false)
    private Pharmacy pharmacy;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    @Column(name = "ACTIVE_PRINCIPLE", nullable = true, length = 255)
    private String activePrinciple;

    @Column(name = "PRESENTATION", nullable = true, length = 255)
    private String presentation;

    @Column(name = "STOCK", nullable = true)
    private Integer stock;

    @Column(name = "BRAND", nullable = true, length = 100)
    private String brand;

    // Constructor por defecto
    public Medicine() {}

    // Getters y Setters
    public Long getIdMedicine() {
        return idMedicine;
    }

    public void setIdMedicine(Long idMedicine) {
        this.idMedicine = idMedicine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getActivePrinciple() {
        return activePrinciple;
    }

    public void setActivePrinciple(String activePrinciple) {
        this.activePrinciple = activePrinciple;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
