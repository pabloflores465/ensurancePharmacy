package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "POLICY")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_POLICY")
    private Long idPolicy;

    @Column(name = "PERCENTAGE")
    private Float percentage;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "COST")
    private Float cost;

    @Column(name = "ENABLED")
    private Integer enabled;

    // Constructor por defecto
    public Policy() {
    }

    // Constructor con parámetros
    public Policy(Float percentage, Date creationDate, Date expDate, Float cost, Integer enabled) {
        this.percentage = percentage;
        this.creationDate = creationDate;
        this.expDate = expDate;
        this.cost = cost;
        this.enabled = enabled;
    }

    // Getters y Setters
    public Long getIdPolicy() {
        return idPolicy;
    }

    public void setIdPolicy(Long idPolicy) {
        this.idPolicy = idPolicy;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}