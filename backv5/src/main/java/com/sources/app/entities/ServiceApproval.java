package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SERVICE_APPROVALS")
public class ServiceApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_APPROVAL")
    private Long idApproval;

    @Column(name = "APPROVAL_CODE", length = 20, nullable = false, unique = true)
    private String approvalCode;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ID_HOSPITAL", nullable = false)
    private Hospital hospital;

    @Column(name = "SERVICE_ID", nullable = false)
    private String serviceId;

    @Column(name = "SERVICE_NAME", nullable = false)
    private String serviceName;

    @Column(name = "SERVICE_DESCRIPTION")
    private String serviceDescription;

    @Column(name = "SERVICE_COST", nullable = false)
    private Double serviceCost;

    @Column(name = "COVERED_AMOUNT", nullable = false)
    private Double coveredAmount;

    @Column(name = "PATIENT_AMOUNT", nullable = false)
    private Double patientAmount;

    @Column(name = "PRESCRIPTION_ID")
    private String prescriptionId;
    
    @Column(name = "PRESCRIPTION_TOTAL")
    private Double prescriptionTotal;
    
    @Column(name = "STATUS", nullable = false, length = 20)
    private String status; // "APPROVED", "REJECTED", "PENDING", "COMPLETED"
    
    @Column(name = "REJECTION_REASON")
    private String rejectionReason;
    
    @Column(name = "APPROVAL_DATE", nullable = false)
    private Date approvalDate;
    
    @Column(name = "COMPLETED_DATE")
    private Date completedDate;
    
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        approvalDate = new Date();
    }

    // Constructores
    public ServiceApproval() {
    }

    // Getters y Setters
    public Long getIdApproval() {
        return idApproval;
    }

    public void setIdApproval(Long idApproval) {
        this.idApproval = idApproval;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
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

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
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

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    
    public Double getPrescriptionTotal() {
        return prescriptionTotal;
    }

    public void setPrescriptionTotal(Double prescriptionTotal) {
        this.prescriptionTotal = prescriptionTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
} 