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

    @Column(name = "ID_HOSPITAL", nullable = false)
    private Long idHospital;

    @Column(name = "ID_USER", nullable = false)
    private Long idUser;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPOINTMENT_DATE", nullable = false)
    private Date appointmentDate;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Appointment() {}

    // Getters y Setters
    public Long getIdAppointment() { return idAppointment; }
    public void setIdAppointment(Long idAppointment) { this.idAppointment = idAppointment; }

    public Long getIdHospital() { return idHospital; }
    public void setIdHospital(Long idHospital) { this.idHospital = idHospital; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
