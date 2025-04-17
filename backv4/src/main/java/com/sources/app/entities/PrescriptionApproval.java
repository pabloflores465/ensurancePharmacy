package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PRESCRIPTION_APPROVALS")
public class PrescriptionApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_APPROVAL")
    private Long idApproval;

    @Column(name = "AUTHORIZATION_NUMBER", unique = true, nullable = false, length = 50)
    private String authorizationNumber;

    @Column(name = "ID_USER", nullable = false)
    private Long idUser;

    @Column(name = "PRESCRIPTION_ID_HOSPITAL", nullable = true, length = 100) // ID de la receta en el sistema del hospital
    private String prescriptionIdHospital;

    @Column(name = "PRESCRIPTION_DETAILS", length = 2000) // Puede ser JSON o texto
    private String prescriptionDetails;

    @Column(name = "PRESCRIPTION_COST", nullable = false)
    private Double prescriptionCost;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPROVAL_DATE", nullable = false)
    private Date approvalDate;

    @Column(name = "STATUS", nullable = false, length = 20) // APPROVED, REJECTED
    private String status;

    @Column(name = "REJECTION_REASON", length = 255)
    private String rejectionReason;

    // Constructor
    public PrescriptionApproval() {
        this.approvalDate = new Date();
    }

    // Getters y Setters
    public Long getIdApproval() { return idApproval; }
    public void setIdApproval(Long idApproval) { this.idApproval = idApproval; }

    public String getAuthorizationNumber() { return authorizationNumber; }
    public void setAuthorizationNumber(String authorizationNumber) { this.authorizationNumber = authorizationNumber; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public String getPrescriptionIdHospital() { return prescriptionIdHospital; }
    public void setPrescriptionIdHospital(String prescriptionIdHospital) { this.prescriptionIdHospital = prescriptionIdHospital; }

    public String getPrescriptionDetails() { return prescriptionDetails; }
    public void setPrescriptionDetails(String prescriptionDetails) { this.prescriptionDetails = prescriptionDetails; }

    public Double getPrescriptionCost() { return prescriptionCost; }
    public void setPrescriptionCost(Double prescriptionCost) { this.prescriptionCost = prescriptionCost; }

    public Date getApprovalDate() { return approvalDate; }
    public void setApprovalDate(Date approvalDate) { this.approvalDate = approvalDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
} 