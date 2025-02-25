package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "PRESCRIPTION")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRESCRIPTION")
    private Long idPrescription;

    // Relación ManyToOne con Hospital
    @ManyToOne
    @JoinColumn(name = "ID_HOSPITAL", referencedColumnName = "ID_HOSPITAL")
    private Hospital hospital;

    // Relación ManyToOne con User
    @ManyToOne
    @JoinColumn(name = "ID_USER", referencedColumnName = "ID_USER")
    private User user;

    @Column(name = "APPROVED")
    private Character approved;

    // Constructor vacío
    public Prescription() {
    }

    // Constructor con campos
    public Prescription(Hospital hospital, User user, Character approved) {
        this.hospital = hospital;
        this.user = user;
        this.approved = approved;
    }

    // Getters y setters
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

    public Character getApproved() {
        return approved;
    }

    public void setApproved(Character approved) {
        this.approved = approved;
    }
}
