package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDERS_MEDICINE")
public class OrderMedicine {

    @EmbeddedId
    private OrderMedicineId id;

    // Relación ManyToOne a Orders
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "ID_ORDER", referencedColumnName = "ID_ORDER")
    private Orders orders;

    // Relación ManyToOne a Medicine
    @ManyToOne
    @MapsId("medicineId")
    @JoinColumn(name = "ID_MEDICINE", referencedColumnName = "ID_MEDICINE")
    private Medicine medicine;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "COST")
    private Double cost;

    @Column(name = "TOTAL")
    private String total;

    public OrderMedicine() {
    }

    public OrderMedicine(Orders orders, Medicine medicine, Integer quantity, Double cost, String total) {
        this.orders = orders;
        this.medicine = medicine;
        this.quantity = quantity;
        this.cost = cost;
        this.total = total;
        this.id = new OrderMedicineId(orders.getIdOrder(), medicine.getIdMedicine());
    }

    public OrderMedicineId getId() {
        return id;
    }

    public void setId(OrderMedicineId id) {
        this.id = id;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
        if (this.id == null) {
            this.id = new OrderMedicineId();
        }
        this.id.setOrderId(orders.getIdOrder());
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
        if (this.id == null) {
            this.id = new OrderMedicineId();
        }
        this.id.setMedicineId(medicine.getIdMedicine());
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
