package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "MEDICINE")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICINE")
    private Long idMedicine;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ACTIVE_MEDICAMENT")
    private String activeMedicament;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "CONCENTRATION")
    private String concentration;

    @Column(name = "PRESENTACION")
    private String presentacion;

    @Column(name = "STOCK")
    private Integer stock;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "PRESCRIPTION")
    private Boolean prescription;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "SOLD_UNITS")
    private Integer soldUnits;

    // Constructor vacío
    public Medicine() {
    }

    // Constructor con campos
    public Medicine(String name, String activeMedicament, String description, String image,
                    String concentration, String presentacion, Integer stock, String brand,
                    Boolean prescription, Double price, Integer soldUnits) {
        this.name = name;
        this.activeMedicament = activeMedicament;
        this.description = description;
        this.image = image;
        this.concentration = concentration;
        this.presentacion = presentacion;
        this.stock = stock;
        this.brand = brand;
        this.prescription = prescription;
        this.price = price;
        this.soldUnits = soldUnits;
    }

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

    public String getActiveMedicament() {
        return activeMedicament;
    }

    public void setActiveMedicament(String activeMedicament) {
        this.activeMedicament = activeMedicament;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
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

    public Boolean getPrescription() {
        return prescription;
    }

    public void setPrescription(Boolean prescription) {
        this.prescription = prescription;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSoldUnits() {
        return soldUnits;
    }

    public void setSoldUnits(Integer soldUnits) {
        this.soldUnits = soldUnits;
    }
}
