package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "BILL")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_BILL")
    private Long idBill;

    // Relación ManyToOne a Prescription
    @ManyToOne
    @JoinColumn(name = "ID_PRESCRIPTION", referencedColumnName = "ID_PRESCRIPTION")
    private Prescription prescription;

    @Column(name = "TAXES")
    private Double taxes;

    @Column(name = "SUBTOTAL")
    private Double subtotal;

    @Column(name = "COPAY")
    private Double copay;

    @Column(name = "TOTAL")
    private String total;

    // Constructor vacío
    public Bill() {
    }

    // Constructor con campos
    public Bill(Prescription prescription, Double taxes, Double subtotal, Double copay, String total) {
        this.prescription = prescription;
        this.taxes = taxes;
        this.subtotal = subtotal;
        this.copay = copay;
        this.total = total;
    }

    // Getters y setters
    public Long getIdBill() {
        return idBill;
    }

    public void setIdBill(Long idBill) {
        this.idBill = idBill;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Double getTaxes() {
        return taxes;
    }

    public void setTaxes(Double taxes) {
        this.taxes = taxes;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getCopay() {
        return copay;
    }

    public void setCopay(Double copay) {
        this.copay = copay;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
