package com.sources.app.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderMedicineId implements Serializable {

    @Column(name = "ID_ORDER")
    private Long orderId;

    @Column(name = "ID_MEDICINE")
    private Long medicineId;

    public OrderMedicineId() {
    }

    public OrderMedicineId(Long orderId, Long medicineId) {
        this.orderId = orderId;
        this.medicineId = medicineId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
        if (!(o instanceof OrderMedicineId)) return false;
        OrderMedicineId that = (OrderMedicineId) o;
        return Objects.equals(getOrderId(), that.getOrderId()) &&
                Objects.equals(getMedicineId(), that.getMedicineId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderId(), getMedicineId());
    }
}
