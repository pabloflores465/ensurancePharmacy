package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "SUBCATEGORY")
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SUBCATEGORY")
    private Long idSubcategory;

    @Column(name = "NAME")
    private String name;

    // Constructor vacío
    public Subcategory() {
    }

    // Constructor con campos
    public Subcategory(String name) {
        this.name = name;
    }

    // Getters y setters
    public Long getIdSubcategory() {
        return idSubcategory;
    }

    public void setIdSubcategory(Long idSubcategory) {
        this.idSubcategory = idSubcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
