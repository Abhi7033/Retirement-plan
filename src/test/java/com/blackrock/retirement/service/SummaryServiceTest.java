package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests SummaryService - spending analysis, readiness score, tips generation
// Command: mvn test -Dtest=SummaryServiceTest

import com.blackrock.retirement.dto.SummaryResponse;
import com.blackrock.retirement.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryServiceTest {

    private SummaryService service;

    @BeforeEach
    void setUp() {
        service = new SummaryService();
    }

    @Test
    @DisplayName("Should handle empty transaction list")
    void testEmptyTransactions() {
        SummaryResponse result = service.analyzeSummary(Collections.emptyList());
        assertEquals(0, result.getTotalTransactions());
        assertEquals("No data", result.getInvestmentReadinessLabel());
    }

    @Test
    @DisplayName("Should handle null transaction list")
    void testNullTransactions() {
        SummaryResponse result = service.analyzeSummary(null);
        assertEquals(0, result.getTotalTransactions());
    }

    @Test
    @DisplayName("Should correctly count valid and invalid transactions")
    void testValidInvalidCounts() {
        Transaction valid = new Transaction("2024-01-15 10:30:00", 150.0, 200.0, 50.0);
        Transaction negative = new Transaction();
        negative.setDate("2024-02-15 10:30:00");
        negative.setAmount(-100.0);

        SummaryResponse result = service.analyzeSummary(Arrays.asList(valid, negative));

        assertEquals(2, result.getTotalTransactions());
        assertEquals(1, result.getValidTransactions());
        assertEquals(1, result.getInvalidTransactions());
    }

    @Test
    @DisplayName("Should calculate spending analysis correctly")
    void testSpendingAnalysis() {
        Transaction t1 = new Transaction("2024-01-15 10:30:00", 150.0, 200.0, 50.0);
        Transaction t2 = new Transaction("2024-02-15 10:30:00", 300.0, 300.0, 0.0);
        Transaction t3 = new Transaction("2024-03-15 10:30:00", 250.0, 300.0, 50.0);

        SummaryResponse result = service.analyzeSummary(Arrays.asList(t1, t2, t3));

        assertEquals(700.0, result.getTotalSpent());
        assertEquals(300.0, result.getHighestSpend());
        assertEquals(150.0, result.getLowestSpend());
        assertEquals("2024-02-15 10:30:00", result.getHighestSpendDate());
        assertEquals("2024-01-15 10:30:00", result.getLowestSpendDate());
    }

    @Test
    @DisplayName("Should calculate savings potential from ceiling rounding")
    void testSavingsPotential() {
        Transaction t1 = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        SummaryResponse result = service.analyzeSummary(Collections.singletonList(t1));

        assertTrue(result.getTotalSavingsPotential() > 0);
        assertTrue(result.getAnnualSavingsProjection() > 0);
    }

    @Test
    @DisplayName("Should produce investment readiness score between 0 and 100")
    void testReadinessScoreRange() {
        Transaction t1 = new Transaction("2024-01-15 10:30:00", 150.0, 200.0, 50.0);
        Transaction t2 = new Transaction("2024-02-15 10:30:00", 200.0, 200.0, 0.0);
        Transaction t3 = new Transaction("2024-03-15 10:30:00", 175.0, 200.0, 25.0);

        SummaryResponse result = service.analyzeSummary(Arrays.asList(t1, t2, t3));

        assertTrue(result.getInvestmentReadinessScore() >= 0);
        assertTrue(result.getInvestmentReadinessScore() <= 100);
        assertNotNull(result.getInvestmentReadinessLabel());
    }

    @Test
    @DisplayName("Should generate actionable tips")
    void testTipsGenerated() {
        Transaction t1 = new Transaction("2024-01-15 10:30:00", 150.0, 200.0, 50.0);
        Transaction t2 = new Transaction("2024-02-15 10:30:00", 200.0, 200.0, 0.0);
        Transaction negative = new Transaction();
        negative.setDate("2024-03-15 10:30:00");
        negative.setAmount(-50.0);

        SummaryResponse result = service.analyzeSummary(Arrays.asList(t1, t2, negative));

        assertNotNull(result.getTips());
        assertFalse(result.getTips().isEmpty());
    }

    @Test
    @DisplayName("Should reject duplicates in summary analysis")
    void testDuplicatesInSummary() {
        Transaction t1 = new Transaction("2024-01-15 10:30:00", 150.0, 200.0, 50.0);
        Transaction t2 = new Transaction("2024-01-15 10:30:00", 300.0, 300.0, 0.0);

        SummaryResponse result = service.analyzeSummary(Arrays.asList(t1, t2));

        assertEquals(1, result.getValidTransactions());
        assertEquals(1, result.getInvalidTransactions());
    }
}
