package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "PRESCRIPTION")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRESCRIPTION")
    private Long idPrescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_HOSPITAL", nullable = false)
    private Hospital hospital;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_MEDICINE", nullable = false)
    private Medicine medicine;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PHARMACY", nullable = false)
    private Pharmacy pharmacy;

    @Temporal(TemporalType.DATE)
    @Column(name = "PRESCRIPTION_DATE", nullable = false)
    private Date prescriptionDate;

    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "COPAY", nullable = false, precision = 10, scale = 2)
    private BigDecimal copay;

    @Column(name = "PRESCRIPTION_COMMENT", nullable = false, length = 255)
    private String prescriptionComment;

    @Column(name = "SECURED", nullable = false)
    private Integer secured;

    @Column(name = "AUTH", nullable = false, length = 100)
    private String auth;

    // Constructor por defecto
    public Prescription() {}

    // Getters y Setters
    public Long getIdPrescription() {
        return idPrescription;
    }
    public void setIdPrescription(Long idPrescription) {
        this.idPrescription = idPrescription;
    }

    public Hospital getHospital() {
        return hospital;
    }
    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Medicine getMedicine() {
        return medicine;
    }
    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }
    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Date getPrescriptionDate() {
        return prescriptionDate;
    }
    public void setPrescriptionDate(Date prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getCopay() {
        return copay;
    }
    public void setCopay(BigDecimal copay) {
        this.copay = copay;
    }

    public String getPrescriptionComment() {
        return prescriptionComment;
    }
    public void setPrescriptionComment(String prescriptionComment) {
        this.prescriptionComment = prescriptionComment;
    }

    public Integer getSecured() {
        return secured;
    }
    public void setSecured(Integer secured) {
        this.secured = secured;
    }

    public String getAuth() {
        return auth;
    }
    public void setAuth(String auth) {
        this.auth = auth;
    }
}
