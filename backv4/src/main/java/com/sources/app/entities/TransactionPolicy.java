package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "TRANSACTION_POLICY")
public class TransactionPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRANSACTION_POLICY")
    private Long idTransactionPolicy;

    @Column(name = "ID_POLICY", nullable = false)
    private Long idPolicy;

    @Column(name = "ID_USER", nullable = false)
    private Long idUser;

    @Temporal(TemporalType.DATE)
    @Column(name = "PAY_DATE", nullable = false)
    private Date payDate;

    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Constructor por defecto
    public TransactionPolicy() {}

    // Getters y Setters
    public Long getIdTransactionPolicy() { return idTransactionPolicy; }
    public void setIdTransactionPolicy(Long idTransactionPolicy) { this.idTransactionPolicy = idTransactionPolicy; }

    public Long getIdPolicy() { return idPolicy; }
    public void setIdPolicy(Long idPolicy) { this.idPolicy = idPolicy; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public Date getPayDate() { return payDate; }
    public void setPayDate(Date payDate) { this.payDate = payDate; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
