package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORY")
    private Long idCategory;

    @Column(name = "NAME")
    private String name;

    // Constructor vacío
    public Category() {
    }

    // Constructor con campos
    public Category(String name) {
        this.name = name;
    }

    // Getters y setters
    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
