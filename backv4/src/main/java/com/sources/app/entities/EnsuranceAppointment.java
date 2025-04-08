package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidad para almacenar citas del sistema de seguros.
 * Esta tabla tendrá una estructura más simple que no depende de la conversión de IDs.
 */
@Entity
@Table(name = "ENSURANCE_APPOINTMENT")
public class EnsuranceAppointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_APPOINTMENT")
    private Long idAppointment;

    @Column(name = "HOSPITAL_APPOINTMENT_ID", nullable = false)
    private String hospitalAppointmentId;  // ID original de la cita en el hospital (MongoDB)

    @Column(name = "ID_USER", nullable = false)
    private Long idUser;  // ID del usuario en el sistema de seguros

    @Temporal(TemporalType.DATE)
    @Column(name = "APPOINTMENT_DATE", nullable = false)
    private Date appointmentDate;  // Fecha de la cita

    @Column(name = "DOCTOR_NAME")
    private String doctorName;  // Nombre del doctor

    @Column(name = "APPOINTMENT_REASON", length = 500)
    private String reason;  // Motivo de la cita

    // Constructor por defecto
    public EnsuranceAppointment() {}

    // Getters y Setters
    public Long getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(Long idAppointment) {
        this.idAppointment = idAppointment;
    }

    public String getHospitalAppointmentId() {
        return hospitalAppointmentId;
    }

    public void setHospitalAppointmentId(String hospitalAppointmentId) {
        this.hospitalAppointmentId = hospitalAppointmentId;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 