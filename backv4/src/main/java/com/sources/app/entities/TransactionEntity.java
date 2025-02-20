package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TRANSACTIONS")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRANSACTION")
    private Long idTransaction;

    @Column(name = "ID_USER", nullable = false)
    private Long idUser;

    @Column(name = "ID_HOSPITAL", nullable = false)
    private Long idHospital;

    @Temporal(TemporalType.DATE)
    @Column(name = "TRANS_DATE", nullable = false)
    private Date transDate;

    @Column(name = "TOTAL", nullable = false)
    private Double total;

    @Column(name = "COPAY", nullable = false)
    private Double copay;

    @Column(name = "TRANSACTION_COMMENT", nullable = false, length = 255)
    private String transactionComment;

    @Column(name = "RESULT", nullable = false, length = 100)
    private String result;

    @Column(name = "COVERED", nullable = false)
    private Integer covered;

    @Column(name = "AUTH", nullable = false, length = 100)
    private String auth;

    // Constructor por defecto
    public TransactionEntity() {}

    // Getters y Setters
    public Long getIdTransaction() { return idTransaction; }
    public void setIdTransaction(Long idTransaction) { this.idTransaction = idTransaction; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public Long getIdHospital() { return idHospital; }
    public void setIdHospital(Long idHospital) { this.idHospital = idHospital; }

    public Date getTransDate() { return transDate; }
    public void setTransDate(Date transDate) { this.transDate = transDate; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public Double getCopay() { return copay; }
    public void setCopay(Double copay) { this.copay = copay; }

    public String getTransactionComment() { return transactionComment; }
    public void setTransactionComment(String transactionComment) { this.transactionComment = transactionComment; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public Integer getCovered() { return covered; }
    public void setCovered(Integer covered) { this.covered = covered; }

    public String getAuth() { return auth; }
    public void setAuth(String auth) { this.auth = auth; }
}
