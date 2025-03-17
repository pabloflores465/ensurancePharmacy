package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "PRESCRIPTION_MEDICINE")
public class PrescriptionMedicine {

    @EmbeddedId
    private PrescriptionMedicineId id;

    // Relación ManyToOne con Prescription
    @ManyToOne
    @MapsId("prescriptionId")
    @JoinColumn(name = "ID_PRESCRIPTION", referencedColumnName = "ID_PRESCRIPTION")
    private Prescription prescription;

    // Relación ManyToOne con Medicine
    @ManyToOne
    @MapsId("medicineId")
    @JoinColumn(name = "ID_MEDICINE", referencedColumnName = "ID_MEDICINE")
    private Medicine medicine;

    @Column(name = "DOSIS")
    private Double dosis;

    @Column(name = "FRECUENCIA")
    private Double frecuencia;

    @Column(name = "DURACION")
    private Double duracion;

    // Constructor vacío
    public PrescriptionMedicine() {
    }

    // Constructor con campos
    public PrescriptionMedicine(Prescription prescription, Medicine medicine, Double dosis, Double frecuencia, Double duracion) {
        this.prescription = prescription;
        this.medicine = medicine;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
        this.id = new PrescriptionMedicineId(prescription.getIdPrescription(), medicine.getIdMedicine());
    }

    // Getters y setters
    public PrescriptionMedicineId getId() {
        return id;
    }

    public void setId(PrescriptionMedicineId id) {
        this.id = id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
        if (id == null) {
            id = new PrescriptionMedicineId();
        }
        id.setPrescriptionId(prescription.getIdPrescription());
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
        if (id == null) {
            id = new PrescriptionMedicineId();
        }
        id.setMedicineId(medicine.getIdMedicine());
    }

    public Double getDosis() {
        return dosis;
    }

    public void setDosis(Double dosis) {
        this.dosis = dosis;
    }

    public Double getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Double frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Double getDuracion() {
        return duracion;
    }

    public void setDuracion(Double duracion) {
        this.duracion = duracion;
    }
}
