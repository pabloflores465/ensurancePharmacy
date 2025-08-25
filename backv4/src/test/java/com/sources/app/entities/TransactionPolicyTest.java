 package com.sources.app.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionPolicyTest {

    private TransactionPolicy tp;
    private Policy policy;
    private User user;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        tp = new TransactionPolicy();
        policy = mock(Policy.class);
        user = mock(User.class);
    }

    @Test
    void noArgsConstructor_InitializesNulls() {
        TransactionPolicy fresh = new TransactionPolicy();
        assertNull(fresh.getIdTransactionPolicy());
        assertNull(fresh.getPolicy());
        assertNull(fresh.getUser());
        assertNull(fresh.getPayDate());
        assertNull(fresh.getTotal());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        Long id = 42L;
        Date payDate = new Date();
        BigDecimal total = new BigDecimal("1234.56");

        tp.setIdTransactionPolicy(id);
        tp.setPolicy(policy);
        tp.setUser(user);
        tp.setPayDate(payDate);
        tp.setTotal(total);

        assertEquals(id, tp.getIdTransactionPolicy());
        assertEquals(policy, tp.getPolicy());
        assertEquals(user, tp.getUser());
        assertEquals(payDate, tp.getPayDate());
        assertEquals(total, tp.getTotal());
    }

    @Test
    void idSetter_AllowsNullAndValues() {
        tp.setIdTransactionPolicy(null);
        assertNull(tp.getIdTransactionPolicy());
        tp.setIdTransactionPolicy(1L);
        assertEquals(1L, tp.getIdTransactionPolicy());
    }

    @Test
    void total_PrecisionIsMaintained() {
        BigDecimal value = new BigDecimal("10.00");
        tp.setTotal(value);
        assertEquals(new BigDecimal("10.00"), tp.getTotal());
    }
}