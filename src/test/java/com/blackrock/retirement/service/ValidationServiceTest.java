package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests ValidationService business rules - negative amounts, duplicates, amount limits
// Command: mvn test -Dtest=ValidationServiceTest

import com.blackrock.retirement.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService service;

    @BeforeEach
    void setUp() {
        service = new ValidationService();
    }

    @Test
    @DisplayName("Should accept valid transaction")
    void testValidTransaction() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);

        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Collections.singletonList(txn));

        assertEquals(1, result.getValid().size());
        assertTrue(result.getInvalid().isEmpty());
    }

    @Test
    @DisplayName("Should reject negative amount transaction")
    void testNegativeAmount() {
        Transaction txn = new Transaction();
        txn.setDate("2024-01-15 10:30:00");
        txn.setAmount(-50.0);
        txn.setCeiling(0.0);
        txn.setRemanent(50.0);

        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Collections.singletonList(txn));

        assertTrue(result.getValid().isEmpty());
        assertEquals(1, result.getInvalid().size());
        assertEquals("Negative amounts are not allowed", result.getInvalid().get(0).getMessage());
    }

    @Test
    @DisplayName("Should reject duplicate timestamps")
    void testDuplicateTimestamp() {
        Transaction txn1 = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);
        Transaction txn2 = new Transaction("2024-01-15 10:30:00", 250.0, 300.0, 50.0);

        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Arrays.asList(txn1, txn2));

        assertEquals(1, result.getValid().size());
        assertEquals(1, result.getInvalid().size());
        assertEquals("Duplicate transaction", result.getInvalid().get(0).getMessage());
    }

    @Test
    @DisplayName("Should reject amount exceeding 500000")
    void testAmountExceedsMaximum() {
        Transaction txn = new Transaction("2024-01-15 10:30:00", 500000.0, 500000.0, 0.0);

        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Collections.singletonList(txn));

        assertTrue(result.getValid().isEmpty());
        assertEquals(1, result.getInvalid().size());
        assertEquals("Amount exceeds maximum allowed value", result.getInvalid().get(0).getMessage());
    }

    @Test
    @DisplayName("Should handle mixed valid and invalid transactions")
    void testMixedValidAndInvalid() {
        Transaction valid = new Transaction("2024-01-15 10:30:00", 150.75, 200.0, 49.25);
        Transaction negative = new Transaction();
        negative.setDate("2024-02-15 10:30:00");
        negative.setAmount(-100.0);
        Transaction tooLarge = new Transaction("2024-03-15 10:30:00", 600000.0, 600000.0, 0.0);

        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Arrays.asList(valid, negative, tooLarge));

        assertEquals(1, result.getValid().size());
        assertEquals(2, result.getInvalid().size());
    }

    @Test
    @DisplayName("Should handle empty transaction list")
    void testEmptyTransactionList() {
        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Collections.emptyList());

        assertTrue(result.getValid().isEmpty());
        assertTrue(result.getInvalid().isEmpty());
    }

    @Test
    @DisplayName("First occurrence of duplicate should be valid, second invalid")
    void testDuplicateOrdering() {
        Transaction txn1 = new Transaction("2024-01-15 10:30:00", 150.0, 200.0, 50.0);
        Transaction txn2 = new Transaction("2024-01-15 10:30:00", 300.0, 300.0, 0.0);

        ValidationService.ValidationResult result =
                service.validateTransactions(50000, Arrays.asList(txn1, txn2));

        assertEquals(150.0, result.getValid().get(0).getAmount());
        assertEquals(300.0, result.getInvalid().get(0).getAmount());
    }
}
