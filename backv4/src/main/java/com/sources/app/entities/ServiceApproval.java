package com.sources.app.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entidad que representa una aprobación de servicio médico
 */
@Entity
@Table(name = "service_approvals")
public class ServiceApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_approval_seq")
    @SequenceGenerator(name = "service_approval_seq", sequenceName = "service_approval_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "approval_code", unique = true, nullable = false)
    private String approvalCode;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;
    
    @Column(name = "service_id", nullable = false)
    private String serviceId;  // ID del servicio en el sistema del hospital
    
    @Column(name = "service_name", nullable = false)
    private String serviceName;
    
    @Column(name = "service_cost", nullable = false)
    private Double serviceCost;
    
    @Column(name = "covered_amount", nullable = false)
    private Double coveredAmount;
    
    @Column(name = "patient_amount", nullable = false)
    private Double patientAmount;
    
    @Column(name = "status", nullable = false)
    private String status;  // PENDING, APPROVED, REJECTED, COMPLETED
    
    @Column(name = "approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvalDate;
    
    @Column(name = "completed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "prescription_id")
    private Long prescriptionId;
    
    @Column(name = "prescription_total")
    private Double prescriptionTotal;

    public ServiceApproval() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(Double serviceCost) {
        this.serviceCost = serviceCost;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Double getPrescriptionTotal() {
        return prescriptionTotal;
    }

    public void setPrescriptionTotal(Double prescriptionTotal) {
        this.prescriptionTotal = prescriptionTotal;
    }
} 