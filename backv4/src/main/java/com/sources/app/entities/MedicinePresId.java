package com.sources.app.entities;

import java.io.Serializable;
import java.util.Objects;

public final class MedicinePresId implements Serializable {

    private final Long prescription; // Corresponde al id de Prescription
    private final Long medicine;     // Corresponde al id de Medicine

    // Constructor por defecto
    public MedicinePresId() {
        this.prescription = null;
        this.medicine = null;
    }

    public MedicinePresId(Long prescription, Long medicine) {
        this.prescription = prescription;
        this.medicine = medicine;
    }

    // equals() y hashCode() son obligatorios
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MedicinePresId)) {
            return false;
        }
        MedicinePresId that = (MedicinePresId) o;
        return Objects.equals(prescription, that.prescription)
                && Objects.equals(medicine, that.medicine);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(prescription, medicine);
    }
}
