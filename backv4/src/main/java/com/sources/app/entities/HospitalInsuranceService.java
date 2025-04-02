package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "HOSPITAL_INSURANCE_SERVICE")
public class HospitalInsuranceService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HOSPITAL_SERVICE")
    private Long idHospitalService;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_HOSPITAL", nullable = false)
    private Hospital hospital;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_INSURANCE_SERVICE", nullable = false)
    private InsuranceService insuranceService;

    @Column(name = "APPROVED", nullable = false)
    private Integer approved;

    @Column(name = "APPROVAL_DATE")
    private java.util.Date approvalDate;

    @Column(name = "NOTES", length = 255)
    private String notes;

    // Constructor por defecto
    public HospitalInsuranceService() {}

    // Getters y Setters
    public Long getIdHospitalService() {
        return idHospitalService;
    }

    public void setIdHospitalService(Long idHospitalService) {
        this.idHospitalService = idHospitalService;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public InsuranceService getInsuranceService() {
        return insuranceService;
    }

    public void setInsuranceService(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public java.util.Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(java.util.Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 