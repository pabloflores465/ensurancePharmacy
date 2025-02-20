package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "CATEGORY")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORY")
    private Long idCategory;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "ENABLED", nullable = false)
    private Integer enabled;

    // Constructor por defecto
    public Category() {}

    // Getters y Setters
    public Long getIdCategory() { return idCategory; }
    public void setIdCategory(Long idCategory) { this.idCategory = idCategory; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
