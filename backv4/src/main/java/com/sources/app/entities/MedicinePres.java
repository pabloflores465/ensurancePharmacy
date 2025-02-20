package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "MEDICINE_PRES")
public class MedicinePres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICINE_PRESCRIPTION")
    private Long idMedicinePrescription;

    @Column(name = "ID_PRESCRIPTION", nullable = false)
    private Long idPrescription;

    @Column(name = "ID_MEDICINE", nullable = false)
    private Long idMedicine;

    // Constructor por defecto
    public MedicinePres() {}

    // Getters y Setters
    public Long getIdMedicinePrescription() { return idMedicinePrescription; }
    public void setIdMedicinePrescription(Long idMedicinePrescription) { this.idMedicinePrescription = idMedicinePrescription; }

    public Long getIdPrescription() { return idPrescription; }
    public void setIdPrescription(Long idPrescription) { this.idPrescription = idPrescription; }

    public Long getIdMedicine() { return idMedicine; }
    public void setIdMedicine(Long idMedicine) { this.idMedicine = idMedicine; }
}
