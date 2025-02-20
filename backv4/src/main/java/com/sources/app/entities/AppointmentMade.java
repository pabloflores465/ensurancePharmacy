package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "APPOINTMENT_MADE")
public class AppointmentMade {
    @Id
    @Column(name = "ID_CITA", nullable = false)
    private Long idCita;

    @Column(name = "ID_USER", nullable = false)
    private Long idUser;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPOINTMENT_MADE_DATE", nullable = false)
    private Date appointmentMadeDate;

    // Constructor por defecto
    public AppointmentMade() {}

    // Getters y Setters
    public Long getIdCita() { return idCita; }
    public void setIdCita(Long idCita) { this.idCita = idCita; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public Date getAppointmentMadeDate() { return appointmentMadeDate; }
    public void setAppointmentMadeDate(Date appointmentMadeDate) { this.appointmentMadeDate = appointmentMadeDate; }
}
