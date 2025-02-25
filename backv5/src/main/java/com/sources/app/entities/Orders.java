package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDERS")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDER")
    private Long idOrder;

    @Column(name = "STATUS")
    private String status;

    // Constructor vacío
    public Orders() {
    }

    // Constructor con campos
    public Orders(String status) {
        this.status = status;
    }

    // Getters y setters
    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
