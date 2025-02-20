package com.sources.app.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CONFIGURABLE_AMOUNT")
public class ConfigurableAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONFIGURABLE_AMOUNT")
    private Long idConfigurableAmount;

    @Column(name = "PRESCRIPTION_AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal prescriptionAmount;

    // Constructor por defecto
    public ConfigurableAmount() {}

    // Getters y Setters
    public Long getIdConfigurableAmount() { return idConfigurableAmount; }
    public void setIdConfigurableAmount(Long idConfigurableAmount) { this.idConfigurableAmount = idConfigurableAmount; }

    public BigDecimal getPrescriptionAmount() { return prescriptionAmount; }
    public void setPrescriptionAmount(BigDecimal prescriptionAmount) { this.prescriptionAmount = prescriptionAmount; }
}
