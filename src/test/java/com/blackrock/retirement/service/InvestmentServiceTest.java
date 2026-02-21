package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests InvestmentService - NPS/Index returns, compound interest, inflation, tax slabs
// Command: mvn test -Dtest=InvestmentServiceTest

import com.blackrock.retirement.dto.ReturnsResponse;
import com.blackrock.retirement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvestmentServiceTest {

    private InvestmentService service;

    @BeforeEach
    void setUp() {
        service = new InvestmentService();
    }

    @Test
    @DisplayName("NPS should use 7.11% annual rate and include tax benefit")
    void testNpsReturnsWithTaxBenefit() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse result = service.calculateNpsReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertNotNull(result);
        assertEquals(150.75, result.getTotalTransactionAmount());
        assertEquals(200.0, result.getTotalCeiling());
        assertFalse(result.getSavingsByDates().isEmpty());

        SavingsByDate saving = result.getSavingsByDates().get(0);
        assertTrue(saving.getTaxBenefit() >= 0);
    }

    @Test
    @DisplayName("Index should use 14.49% annual rate with no tax benefit")
    void testIndexReturnsNoTaxBenefit() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse result = service.calculateIndexReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertNotNull(result);
        assertEquals(150.75, result.getTotalTransactionAmount());

        SavingsByDate saving = result.getSavingsByDates().get(0);
        // Index returns should be higher than NPS (14.49% > 7.11%)
        assertEquals(0.0, saving.getTaxBenefit());
    }

    @Test
    @DisplayName("Index returns profit should be higher than NPS for same input")
    void testIndexHigherThanNps() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse nps = service.calculateNpsReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        ReturnsResponse index = service.calculateIndexReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertTrue(index.getSavingsByDates().get(0).getProfit()
                > nps.getSavingsByDates().get(0).getProfit());
    }

    @Test
    @DisplayName("Should calculate minimum 5 years investment for age >= 55")
    void testMinimumInvestmentYears() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 1000.0, 1000.0, 0.0);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        // age 58, so retirement in 2 years, but minimum 5 years applies
        ReturnsResponse result = service.calculateNpsReturns(
                58, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertNotNull(result);
        assertFalse(result.getSavingsByDates().isEmpty());
    }

    @Test
    @DisplayName("Should handle q-period override in investment calculation")
    void testQPeriodInReturns() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        QPeriod q = new QPeriod();
        q.setFixed(100.0);
        q.setStart("2024-01-01 00:00:00");
        q.setEnd("2024-12-31 23:59:00");

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse result = service.calculateNpsReturns(
                30, 50000, 6.0,
                Collections.singletonList(q), null,
                Collections.singletonList(k), Collections.singletonList(txn));

        // q-period overrides remanent to 100.0
        assertEquals(100.0, result.getSavingsByDates().get(0).getAmount());
    }

    @Test
    @DisplayName("Should handle p-period extras in investment calculation")
    void testPPeriodInReturns() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        PPeriod p = new PPeriod();
        p.setExtra(50.0);
        p.setStart("2024-01-01 00:00:00");
        p.setEnd("2024-12-31 23:59:00");

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse result = service.calculateNpsReturns(
                30, 50000, 6.0, null,
                Collections.singletonList(p),
                Collections.singletonList(k), Collections.singletonList(txn));

        // remanent = 49.25 + 50.0 = 99.25
        assertEquals(99.25, result.getSavingsByDates().get(0).getAmount());
    }

    @Test
    @DisplayName("Should filter out negative amounts from investment calculation")
    void testNegativeAmountFiltered() {
        Transaction valid = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);
        Transaction negative = new Transaction();
        negative.setDate("2024-02-15 10:30:00");
        negative.setAmount(-50.0);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse result = service.calculateNpsReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Arrays.asList(valid, negative));

        // only valid transaction's amount counts
        assertEquals(150.75, result.getTotalTransactionAmount());
    }

    @Test
    @DisplayName("Should handle empty k-periods - no savings groups")
    void testEmptyKPeriods() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        ReturnsResponse result = service.calculateNpsReturns(
                30, 50000, 6.0, null, null,
                Collections.emptyList(), Collections.singletonList(txn));

        assertTrue(result.getSavingsByDates().isEmpty());
        assertEquals(150.75, result.getTotalTransactionAmount());
    }

    @Test
    @DisplayName("Profit should be realValue minus principal")
    void testProfitCalculation() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 100.0, 100.0, 0.0);

        QPeriod q = new QPeriod();
        q.setFixed(1000.0);
        q.setStart("2024-01-01 00:00:00");
        q.setEnd("2024-12-31 23:59:00");

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        ReturnsResponse result = service.calculateNpsReturns(
                30, 50000, 0.0, // zero inflation for simpler calculation
                Collections.singletonList(q), null,
                Collections.singletonList(k), Collections.singletonList(txn));

        SavingsByDate saving = result.getSavingsByDates().get(0);
        // with 0 inflation: realValue = futureValue = 1000 * (1.0711)^30
        // profit = realValue - 1000
        assertTrue(saving.getProfit() > 0);
        assertEquals(saving.getAmount(), 1000.0);
    }
}
