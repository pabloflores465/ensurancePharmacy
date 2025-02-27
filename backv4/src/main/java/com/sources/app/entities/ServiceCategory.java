package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "SERVICE_CATEGORY")
@IdClass(ServiceCategoryId.class)
public class ServiceCategory {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_SERVICE", nullable = false)
    private Service service;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CATEGORY", nullable = false)
    private Category category;

    // Constructor por defecto
    public ServiceCategory() {}

    // Getters y Setters
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
