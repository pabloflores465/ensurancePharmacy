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

    // Relación ManyToOne con Policy
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_POLICY", nullable = false)
    private Policy policy;

    // Relación ManyToOne con User
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(name = "PAY_DATE", nullable = false)
    private Date payDate;

    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Constructor por defecto
    public TransactionPolicy() {}

    // Getters y Setters
    public Long getIdTransactionPolicy() {
        return idTransactionPolicy;
    }
    public void setIdTransactionPolicy(Long idTransactionPolicy) {
        this.idTransactionPolicy = idTransactionPolicy;
    }

    public Policy getPolicy() {
        return policy;
    }
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Date getPayDate() {
        return payDate;
    }
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
