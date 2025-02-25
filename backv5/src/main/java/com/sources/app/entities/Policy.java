package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "POLICY")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_POLICY")
    private Long idPolicy;

    @Column(name = "PERCENTAGE")
    private Double percentage;

    @Column(name = "ENABLED")
    private Character enabled;

    // Constructor vacío
    public Policy() {
    }

    // Constructor con campos
    public Policy(Double percentage, Character enabled) {
        this.percentage = percentage;
        this.enabled = enabled;
    }

    // Getters y setters
    public Long getIdPolicy() {
        return idPolicy;
    }

    public void setIdPolicy(Long idPolicy) {
        this.idPolicy = idPolicy;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Character getEnabled() {
        return enabled;
    }

    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }
}
