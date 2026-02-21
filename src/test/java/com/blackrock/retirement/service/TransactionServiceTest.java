package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests TransactionService parsing logic - ceiling calculation and timestamp truncation
// Command: mvn test -Dtest=TransactionServiceTest

import com.blackrock.retirement.model.Expense;
import com.blackrock.retirement.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService();
    }

    @Test
    @DisplayName("Should round amount up to next multiple of 100")
    void testCeilingCalculation() {
        assertEquals(200.0, service.calculateCeiling(150.75));
        assertEquals(100.0, service.calculateCeiling(50.0));
        assertEquals(100.0, service.calculateCeiling(100.0));
        assertEquals(300.0, service.calculateCeiling(201.0));
    }

    @Test
    @DisplayName("Should parse single expense and compute correct ceiling and remanent")
    void testParseSingleExpense() {
        Expense expense = new Expense();
        expense.setTimestamp("2024-02-15 12:30:45");
        expense.setAmount(150.75);

        List<Transaction> result = service.parseExpenses(Collections.singletonList(expense));

        assertEquals(1, result.size());
        Transaction txn = result.get(0);
        assertEquals("2024-02-15 12:30:00", txn.getDate());
        assertEquals(150.75, txn.getAmount());
        assertEquals(200.0, txn.getCeiling());
        assertEquals(49.25, txn.getRemanent());
    }

    @Test
    @DisplayName("Should truncate seconds from timestamp")
    void testSecondsTruncation() {
        Expense expense = new Expense();
        expense.setTimestamp("2024-01-01 09:15:59");
        expense.setAmount(100.0);

        List<Transaction> result = service.parseExpenses(Collections.singletonList(expense));

        assertEquals("2024-01-01 09:15:00", result.get(0).getDate());
    }

    @Test
    @DisplayName("Should handle timestamp without seconds")
    void testTimestampWithoutSeconds() {
        Expense expense = new Expense();
        expense.setTimestamp("2024-01-01 09:15");
        expense.setAmount(250.0);

        List<Transaction> result = service.parseExpenses(Collections.singletonList(expense));

        assertEquals("2024-01-01 09:15:00", result.get(0).getDate());
        assertEquals(300.0, result.get(0).getCeiling());
        assertEquals(50.0, result.get(0).getRemanent());
    }

    @Test
    @DisplayName("Should handle exact multiple of 100 - zero remanent")
    void testExactMultipleOf100() {
        Expense expense = new Expense();
        expense.setTimestamp("2024-03-01 10:00:00");
        expense.setAmount(300.0);

        List<Transaction> result = service.parseExpenses(Collections.singletonList(expense));

        assertEquals(300.0, result.get(0).getCeiling());
        assertEquals(0.0, result.get(0).getRemanent());
    }

    @Test
    @DisplayName("Should parse multiple expenses correctly")
    void testMultipleExpenses() {
        Expense e1 = new Expense();
        e1.setTimestamp("2024-01-15 10:30:00");
        e1.setAmount(150.75);

        Expense e2 = new Expense();
        e2.setTimestamp("2024-02-20 14:45:30");
        e2.setAmount(200.0);

        Expense e3 = new Expense();
        e3.setTimestamp("2024-03-10 09:00:00");
        e3.setAmount(175.50);

        List<Transaction> result = service.parseExpenses(Arrays.asList(e1, e2, e3));

        assertEquals(3, result.size());
        assertEquals(200.0, result.get(0).getCeiling());
        assertEquals(200.0, result.get(1).getCeiling());
        assertEquals(200.0, result.get(2).getCeiling());
    }

    @Test
    @DisplayName("Should handle empty expense list")
    void testEmptyExpenseList() {
        List<Transaction> result = service.parseExpenses(Collections.emptyList());
        assertTrue(result.isEmpty());
    }
}
