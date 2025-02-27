package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "TOTAL_HOSPITAL")
public class TotalHospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TOTAL_HOSPITAL")
    private Long idTotalHospital;

    // Relación ManyToOne con Hospital (la columna ID_HOSPITAL es llave foránea)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_HOSPITAL", nullable = false)
    private Hospital hospital;

    @Temporal(TemporalType.DATE)
    @Column(name = "TOTAL_DATE", nullable = false)
    private Date totalDate;

    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Constructor por defecto
    public TotalHospital() {}

    // Getters y Setters
    public Long getIdTotalHospital() {
        return idTotalHospital;
    }

    public void setIdTotalHospital(Long idTotalHospital) {
        this.idTotalHospital = idTotalHospital;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
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
