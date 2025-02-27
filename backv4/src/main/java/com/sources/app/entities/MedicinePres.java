package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "MEDICINE_PRES")
@IdClass(MedicinePresId.class)
public class MedicinePres {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PRESCRIPTION", nullable = false)
    private Prescription prescription;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_MEDICINE", nullable = false)
    private Medicine medicine;

    // Constructor por defecto
    public MedicinePres() {}

    // Getters y Setters
    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
}
