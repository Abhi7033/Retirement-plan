package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests InvestmentService compare functionality - NPS vs Index side-by-side
// Command: mvn test -Dtest=CompareServiceTest

import com.blackrock.retirement.dto.CompareResponse;
import com.blackrock.retirement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CompareServiceTest {

    private InvestmentService service;

    @BeforeEach
    void setUp() {
        service = new InvestmentService();
    }

    @Test
    @DisplayName("Compare should return both NPS and Index results")
    void testCompareReturnsBothResults() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        CompareResponse result = service.compareReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertNotNull(result.getNpsSavings());
        assertNotNull(result.getIndexSavings());
        assertFalse(result.getNpsSavings().isEmpty());
        assertFalse(result.getIndexSavings().isEmpty());
    }

    @Test
    @DisplayName("Index effective gain should be higher than NPS for young users")
    void testIndexHigherForYoungUsers() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        CompareResponse result = service.compareReturns(
                25, 30000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertTrue(result.getIndexEffectiveGain() > result.getNpsEffectiveGain());
    }

    @Test
    @DisplayName("Young users should get Aggressive risk profile")
    void testAggressiveRiskProfile() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        CompareResponse result = service.compareReturns(
                28, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertEquals("Aggressive", result.getRiskProfile());
        assertEquals(70, result.getSuggestedIndexPercent());
        assertEquals(30, result.getSuggestedNpsPercent());
    }

    @Test
    @DisplayName("Older users should get Conservative risk profile")
    void testConservativeRiskProfile() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        CompareResponse result = service.compareReturns(
                52, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertEquals("Conservative", result.getRiskProfile());
        assertTrue(result.getSuggestedNpsPercent() > result.getSuggestedIndexPercent());
    }

    @Test
    @DisplayName("Should include recommendation and reasoning")
    void testRecommendationPresent() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        CompareResponse result = service.compareReturns(
                35, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertNotNull(result.getRecommendation());
        assertFalse(result.getRecommendation().isEmpty());
        assertNotNull(result.getReasoning());
        assertFalse(result.getReasoning().isEmpty());
    }

    @Test
    @DisplayName("NPS/Index percent split should sum to 100")
    void testPercentSplitSumsTo100() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        CompareResponse result = service.compareReturns(
                40, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertEquals(100, result.getSuggestedNpsPercent() + result.getSuggestedIndexPercent());
    }

    @Test
    @DisplayName("High earners should have higher NPS allocation for tax benefit")
    void testHighEarnerNpsBoost() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        KPeriod k = new KPeriod();
        k.setStart("2024-01-01 00:00:00");
        k.setEnd("2024-12-31 23:59:00");

        // high earner: 1.5L monthly = 18L annual (30% bracket)
        CompareResponse highEarner = service.compareReturns(
                30, 150000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        // regular earner
        CompareResponse regularEarner = service.compareReturns(
                30, 50000, 6.0, null, null,
                Collections.singletonList(k), Collections.singletonList(txn));

        assertTrue(highEarner.getSuggestedNpsPercent() > regularEarner.getSuggestedNpsPercent());
    }
}
