package com.sources.app.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PrescriptionMedicineId implements Serializable {

    @Column(name = "ID_PRESCRIPTION")
    private Long prescriptionId;

    @Column(name = "ID_MEDICINE")
    private Long medicineId;

    public PrescriptionMedicineId() {
    }

    public PrescriptionMedicineId(Long prescriptionId, Long medicineId) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrescriptionMedicineId)) return false;
        PrescriptionMedicineId that = (PrescriptionMedicineId) o;
        return Objects.equals(getPrescriptionId(), that.getPrescriptionId()) &&
                Objects.equals(getMedicineId(), that.getMedicineId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrescriptionId(), getMedicineId());
    }
}
