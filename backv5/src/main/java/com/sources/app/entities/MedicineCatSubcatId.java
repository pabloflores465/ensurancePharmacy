package com.sources.app.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MedicineCatSubcatId implements Serializable {

    @Column(name = "ID_MEDICINE")
    private Long medicineId;

    @Column(name = "ID_CATEGORY")
    private Long categoryId;

    @Column(name = "ID_SUBCATEGORY")
    private Long subcategoryId;

    public MedicineCatSubcatId() {
    }

    public MedicineCatSubcatId(Long medicineId, Long categoryId, Long subcategoryId) {
        this.medicineId = medicineId;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicineCatSubcatId)) return false;
        MedicineCatSubcatId that = (MedicineCatSubcatId) o;
        return Objects.equals(getMedicineId(), that.getMedicineId()) &&
                Objects.equals(getCategoryId(), that.getCategoryId()) &&
                Objects.equals(getSubcategoryId(), that.getSubcategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMedicineId(), getCategoryId(), getSubcategoryId());
    }
}
