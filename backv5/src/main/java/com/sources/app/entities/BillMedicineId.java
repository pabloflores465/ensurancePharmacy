package com.sources.app.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BillMedicineId implements Serializable {

    @Column(name = "ID_BILL")
    private Long billId;

    @Column(name = "ID_MEDICINE")
    private Long medicineId;

    public BillMedicineId() {
    }

    public BillMedicineId(Long billId, Long medicineId) {
        this.billId = billId;
        this.medicineId = medicineId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
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
        if (!(o instanceof BillMedicineId)) return false;
        BillMedicineId that = (BillMedicineId) o;
        return Objects.equals(billId, that.billId) &&
                Objects.equals(medicineId, that.medicineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, medicineId);
    }
}
