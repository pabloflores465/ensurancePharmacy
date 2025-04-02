package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "INSURANCE_SERVICES")
public class InsuranceService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSURANCE_SERVICE")
    private Long idInsuranceService;

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

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "COVERAGE_PERCENTAGE", nullable = false)
    private Integer coveragePercentage;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public InsuranceService() {}

    // Getters y Setters
    public Long getIdInsuranceService() {
        return idInsuranceService;
    }

    public void setIdInsuranceService(Long idInsuranceService) {
        this.idInsuranceService = idInsuranceService;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCoveragePercentage() {
        return coveragePercentage;
    }

    public void setCoveragePercentage(Integer coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
} 