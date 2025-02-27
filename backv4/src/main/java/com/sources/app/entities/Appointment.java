package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "APPOINTMENT")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_APPOINTMENT")
    private Long idAppointment;

    // Relación ManyToOne con Hospital
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_HOSPITAL", nullable = false)
    private Hospital hospital;

    // Relación ManyToOne con User
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPOINTMENT_DATE", nullable = false)
    private Date appointmentDate;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Appointment() {}

    // Getters y Setters
    public Long getIdAppointment() {
        return idAppointment;
    }
    public void setIdAppointment(Long idAppointment) {
        this.idAppointment = idAppointment;
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

    public Date getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getEnabled() {
        return enabled;
    }
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
