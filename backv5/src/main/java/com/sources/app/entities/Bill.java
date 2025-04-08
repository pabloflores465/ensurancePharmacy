package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

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

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @Column(name = "COVERED_AMOUNT")
    private Double coveredAmount;

    @Column(name = "PATIENT_AMOUNT")
    private Double patientAmount;

    @Column(name = "INSURANCE_APPROVAL_CODE")
    private String insuranceApprovalCode;

    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "STATUS")
    private String status;

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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getCoveredAmount() {
        return coveredAmount;
    }

    public void setCoveredAmount(Double coveredAmount) {
        this.coveredAmount = coveredAmount;
    }

    public Double getPatientAmount() {
        return patientAmount;
    }

    public void setPatientAmount(Double patientAmount) {
        this.patientAmount = patientAmount;
    }

    public String getInsuranceApprovalCode() {
        return insuranceApprovalCode;
    }

    public void setInsuranceApprovalCode(String insuranceApprovalCode) {
        this.insuranceApprovalCode = insuranceApprovalCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return idBill;
    }
}
