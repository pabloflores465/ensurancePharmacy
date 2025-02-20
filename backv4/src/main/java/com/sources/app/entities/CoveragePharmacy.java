package com.sources.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "COVERAGE_PHARMACY")
public class CoveragePharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COVERAGE_PHARMACY")
    private Long idCoveragePharmacy;

    @Column(name = "ID_PHARMACY", nullable = false)
    private Long idPharmacy;

    @Column(name = "ID_MEDICINE", nullable = false)
    private Long idMedicine;

    @Column(name = "COVERAGE", nullable = false)
    private Integer coverage;

    // Constructor por defecto
    public CoveragePharmacy() {}

    // Getters y Setters
    public Long getIdCoveragePharmacy() { return idCoveragePharmacy; }
    public void setIdCoveragePharmacy(Long idCoveragePharmacy) { this.idCoveragePharmacy = idCoveragePharmacy; }

    public Long getIdPharmacy() { return idPharmacy; }
    public void setIdPharmacy(Long idPharmacy) { this.idPharmacy = idPharmacy; }

    public Long getIdMedicine() { return idMedicine; }
    public void setIdMedicine(Long idMedicine) { this.idMedicine = idMedicine; }

    public Integer getCoverage() { return coverage; }
    public void setCoverage(Integer coverage) { this.coverage = coverage; }
}
