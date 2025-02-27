package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "TOTAL_PHARMACY")
public class TotalPharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TOTAL_PHARMACY")
    private Long idTotalPharmacy;

    // Relación ManyToOne con Pharmacy (la columna ID_PHARMACY es llave foránea)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PHARMACY", nullable = false)
    private Pharmacy pharmacy;

    @Temporal(TemporalType.DATE)
    @Column(name = "TOTAL_DATE", nullable = false)
    private Date totalDate;

    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Constructor por defecto
    public TotalPharmacy() {}

    // Getters y Setters
    public Long getIdTotalPharmacy() {
        return idTotalPharmacy;
    }

    public void setIdTotalPharmacy(Long idTotalPharmacy) {
        this.idTotalPharmacy = idTotalPharmacy;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Date getTotalDate() {
        return totalDate;
    }

    public void setTotalDate(Date totalDate) {
        this.totalDate = totalDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
