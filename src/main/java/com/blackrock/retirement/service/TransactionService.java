package com.blackrock.retirement.service;

import com.blackrock.retirement.model.Expense;
import com.blackrock.retirement.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private static final DateTimeFormatter INPUT_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Parses raw expenses into transactions by calculating the ceiling (next multiple of 100)
     * and the remanent (difference between ceiling and actual amount).
     * Seconds in the timestamp are truncated to produce a clean date output.
     */
    public List<Transaction> parseExpenses(List<Expense> expenses) {
        List<Transaction> transactions = new ArrayList<>();

        for (Expense expense : expenses) {
            LocalDateTime dateTime = LocalDateTime.parse(expense.getTimestamp(), INPUT_FORMATTER);

            // truncate seconds for the output date
            LocalDateTime truncated = dateTime.withSecond(0);
            String formattedDate = truncated.format(OUTPUT_FORMATTER);

            double amount = expense.getAmount();
            double ceiling = calculateCeiling(amount);
            double remanent = ceiling - amount;

            transactions.add(new Transaction(formattedDate, amount, ceiling, remanent));
        }

        return transactions;
    }

    /**
     * Rounds the amount up to the next multiple of 100.
     * If the amount is already a multiple of 100, it stays the same.
     */
    public double calculateCeiling(double amount) {
        return Math.ceil(amount / 100.0) * 100;
    }
}
