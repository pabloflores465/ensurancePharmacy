package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "BILL_MEDICINE")
public class BillMedicine {

    @EmbeddedId
    private BillMedicineId id;

    // Relación ManyToOne a Bill
    @ManyToOne
    @MapsId("billId")
    @JoinColumn(name = "ID_BILL", referencedColumnName = "ID_BILL")
    private Bill bill;

    // Relación ManyToOne a Medicine
    @ManyToOne
    @MapsId("medicineId")
    @JoinColumn(name = "ID_MEDICINE", referencedColumnName = "ID_MEDICINE")
    private Medicine medicine;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "COST")
    private Double cost;

    @Column(name = "COPAY")
    private Double copay;

    @Column(name = "TOTAL")
    private String total;

    public BillMedicine() {
    }

    public BillMedicine(Bill bill, Medicine medicine, Integer quantity, Double cost, Double copay, String total) {
        this.bill = bill;
        this.medicine = medicine;
        this.quantity = quantity;
        this.cost = cost;
        this.copay = copay;
        this.total = total;
        // Inicializamos la clave compuesta
        this.id = new BillMedicineId(bill.getIdBill(), medicine.getIdMedicine());
    }

    public BillMedicineId getId() {
        return id;
    }

    public void setId(BillMedicineId id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
        if (id == null) {
            id = new BillMedicineId();
        }
        id.setBillId(bill.getIdBill());
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
        if (id == null) {
            id = new BillMedicineId();
        }
        id.setMedicineId(medicine.getIdMedicine());
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

    public Double getCopay() {
        return copay;
    }

    public void setCopay(Double copay) {
        this.copay = copay;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
