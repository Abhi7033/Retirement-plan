package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests TemporalFilterService q/p/k period logic, negative/duplicate filtering
// Command: mvn test -Dtest=TemporalFilterServiceTest

import com.blackrock.retirement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemporalFilterServiceTest {

    private TemporalFilterService service;

    @BeforeEach
    void setUp() {
        service = new TemporalFilterService();
    }

    @Test
    @DisplayName("Should compute ceiling and remanent for basic transactions")
    void testBasicFilterWithoutPeriods() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(150.75);

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn), null, null, null, 50000);

        assertEquals(1, result.getValid().size());
        Transaction valid = result.getValid().get(0);
        assertEquals(200.0, valid.getCeiling());
        assertEquals(49.25, valid.getRemanent());
        assertFalse(valid.getInKPeriod());
    }

    @Test
    @DisplayName("Should apply q-period fixed amount override to remanent")
    void testQPeriodOverride() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(150.75);

        QPeriod q = new QPeriod();
        q.setFixed(100.0);
        q.setStart("2024-01-01 00:00:00");
        q.setEnd("2024-02-01 00:00:00");

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn),
                Collections.singletonList(q), null, null, 50000);

        assertEquals(100.0, result.getValid().get(0).getRemanent());
    }

    @Test
    @DisplayName("Should use q-period with latest start when multiple match")
    void testMultipleQPeriods() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(150.75);

        QPeriod q1 = new QPeriod();
        q1.setFixed(50.0);
        q1.setStart("2024-01-01 00:00:00");
        q1.setEnd("2024-02-01 00:00:00");

        QPeriod q2 = new QPeriod();
        q2.setFixed(75.0);
        q2.setStart("2024-01-10 00:00:00");
        q2.setEnd("2024-02-01 00:00:00");

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn),
                Arrays.asList(q1, q2), null, null, 50000);

        // q2 has later start, so its fixed value should apply
        assertEquals(75.0, result.getValid().get(0).getRemanent());
    }

    @Test
    @DisplayName("Should add p-period extras to remanent additively")
    void testPPeriodAddsExtra() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(150.75);

        PPeriod p = new PPeriod();
        p.setExtra(25.0);
        p.setStart("2024-01-01 00:00:00");
        p.setEnd("2024-02-01 00:00:00");

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn), null,
                Collections.singletonList(p), null, 50000);

        // remanent = 49.25 + 25.0 = 74.25
        assertEquals(74.25, result.getValid().get(0).getRemanent());
    }

    @Test
    @DisplayName("Should mark transaction as in k-period when within range")
    void testKPeriodFlag() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(150.75);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-02-01 00:00:00");

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn), null, null,
                Collections.singletonList(k), 50000);

        assertTrue(result.getValid().get(0).getInKPeriod());
    }

    @Test
    @DisplayName("Should mark transaction as not in k-period when outside range")
    void testOutsideKPeriod() {
        Transaction txn = new Transaction();
        txn.setDate("2024-03-15 10:30:00");
        txn.setAmount(150.75);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-02-01 00:00:00");

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn), null, null,
                Collections.singletonList(k), 50000);

        assertFalse(result.getValid().get(0).getInKPeriod());
    }

    @Test
    @DisplayName("Should reject negative amounts in filter")
    void testNegativeAmountRejected() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(-100.0);

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.singletonList(txn), null, null, null, 50000);

        assertTrue(result.getValid().isEmpty());
        assertEquals(1, result.getInvalid().size());
        assertEquals("Negative amounts are not allowed", result.getInvalid().get(0).getMessage());
    }

    @Test
    @DisplayName("Should reject duplicate transactions in filter")
    void testDuplicateRejected() {
        Transaction txn1 = new Transaction();
        txn1.setDate("2024-01-15 10:30:00");
        txn1.setAmount(150.0);

        Transaction txn2 = new Transaction();
        txn2.setDate("2024-01-15 10:30:00");
        txn2.setAmount(200.0);

        TemporalFilterService.FilterResult result = service.filterTransactions(
                Arrays.asList(txn1, txn2), null, null, null, 50000);

        assertEquals(1, result.getValid().size());
        assertEquals(1, result.getInvalid().size());
        assertEquals("Duplicate transaction", result.getInvalid().get(0).getMessage());
    }

    @Test
    @DisplayName("Should handle empty transaction list")
    void testEmptyTransactions() {
        TemporalFilterService.FilterResult result = service.filterTransactions(
                Collections.emptyList(), null, null, null, 50000);

        assertTrue(result.getValid().isEmpty());
        assertTrue(result.getInvalid().isEmpty());
    }
}
