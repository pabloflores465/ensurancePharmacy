package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "SERVICES")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SERVICE")
    private Long idService;

    @Column(name = "ID_HOSPITAL", nullable = false)
    private Long idHospital;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;

    @Column(name = "ID_CATEGORY", nullable = false)
    private Long idCategory;

    @Column(name = "ID_SUBCATEGORY", nullable = false)
    private Long idSubcategory;

    @Column(name = "COST", nullable = false)
    private Double cost;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Service() {}

    // Getters y Setters
    public Long getIdService() { return idService; }
    public void setIdService(Long idService) { this.idService = idService; }

    public Long getIdHospital() { return idHospital; }
    public void setIdHospital(Long idHospital) { this.idHospital = idHospital; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getIdCategory() { return idCategory; }
    public void setIdCategory(Long idCategory) { this.idCategory = idCategory; }

    public Long getIdSubcategory() { return idSubcategory; }
    public void setIdSubcategory(Long idSubcategory) { this.idSubcategory = idSubcategory; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
