package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "SERVICES")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SERVICE")
    private Long idService;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_HOSPITAL", nullable = false)
    private Hospital hospital;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CATEGORY", nullable = false)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_SUBCATEGORY", nullable = false)
    private Category subcategory;

    @Column(name = "COST", nullable = false)
    private Double cost;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Service() {}

    // Getters y Setters
    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Category subcategory) {
        this.subcategory = subcategory;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
