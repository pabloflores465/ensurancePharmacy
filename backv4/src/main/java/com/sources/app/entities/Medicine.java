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

    @Column(name = "ID_PHARMACY", nullable = false)
    private Long idPharmacy;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Medicine() {}

    // Getters y Setters
    public Long getIdMedicine() { return idMedicine; }
    public void setIdMedicine(Long idMedicine) { this.idMedicine = idMedicine; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getIdPharmacy() { return idPharmacy; }
    public void setIdPharmacy(Long idPharmacy) { this.idPharmacy = idPharmacy; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
