package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "MEDICINE_CATSUBCAT")
public class MedicineCatSubcat {

    @EmbeddedId
    private MedicineCatSubcatId id;

    // Relación ManyToOne a Medicine
    @ManyToOne
    @MapsId("medicineId")
    @JoinColumn(name = "ID_MEDICINE", referencedColumnName = "ID_MEDICINE")
    private Medicine medicine;

    // Relación ManyToOne a Category
    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "ID_CATEGORY", referencedColumnName = "ID_CATEGORY")
    private Category category;

    // Relación ManyToOne a Subcategory
    @ManyToOne
    @MapsId("subcategoryId")
    @JoinColumn(name = "ID_SUBCATEGORY", referencedColumnName = "ID_SUBCATEGORY")
    private Subcategory subcategory;

    public MedicineCatSubcat() {
    }

    public MedicineCatSubcat(Medicine medicine, Category category, Subcategory subcategory) {
        this.medicine = medicine;
        this.category = category;
        this.subcategory = subcategory;
        this.id = new MedicineCatSubcatId(
                medicine.getIdMedicine(),
                category.getIdCategory(),
                subcategory.getIdSubcategory()
        );
    }

    public MedicineCatSubcatId getId() {
        return id;
    }

    public void setId(MedicineCatSubcatId id) {
        this.id = id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
        if (id == null) {
            id = new MedicineCatSubcatId();
        }
        id.setMedicineId(medicine.getIdMedicine());
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (id == null) {
            id = new MedicineCatSubcatId();
        }
        id.setCategoryId(category.getIdCategory());
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
        if (id == null) {
            id = new MedicineCatSubcatId();
        }
        id.setSubcategoryId(subcategory.getIdSubcategory());
    }
}
