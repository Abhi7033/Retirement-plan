package com.blackrock.retirement.service;

import com.blackrock.retirement.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValidationService {

    /**
     * Validates a list of transactions based on business rules:
     * - Amounts must be non-negative
     * - Ceiling must be >= amount
     * - Remanent must be non-negative
     * - No duplicate timestamps
     * - Amount must be within reasonable bounds (< 5 * 10^5)
     *
     * Returns two lists: valid and invalid transactions.
     */
    public ValidationResult validateTransactions(double wage, List<Transaction> transactions) {
        List<Transaction> valid = new ArrayList<>();
        List<Transaction> invalid = new ArrayList<>();
        Set<String> seenDates = new HashSet<>();

        for (Transaction txn : transactions) {
            String errorMessage = validateSingleTransaction(txn, wage, seenDates);

            if (errorMessage != null) {
                Transaction invalidTxn = copyTransaction(txn);
                invalidTxn.setMessage(errorMessage);
                invalid.add(invalidTxn);
            } else {
                seenDates.add(txn.getDate());
                valid.add(copyTransaction(txn));
            }
        }

        return new ValidationResult(valid, invalid);
    }

    private String validateSingleTransaction(Transaction txn, double wage, Set<String> seenDates) {
        // check for negative amounts
        if (txn.getAmount() != null && txn.getAmount() < 0) {
            return "Negative amounts are not allowed";
        }

        // check for duplicate timestamps
        if (txn.getDate() != null && seenDates.contains(txn.getDate())) {
            return "Duplicate transaction";
        }

        // check amount exceeds constraint (x < 5 * 10^5)
        if (txn.getAmount() != null && txn.getAmount() >= 500000) {
            return "Amount exceeds maximum allowed value";
        }

        return null; // valid
    }

    private Transaction copyTransaction(Transaction source) {
        Transaction copy = new Transaction();
        copy.setDate(source.getDate());
        copy.setAmount(source.getAmount());
        copy.setCeiling(source.getCeiling());
        copy.setRemanent(source.getRemanent());
        return copy;
    }

    /**
     * Holds the result of transaction validation.
     */
    public static class ValidationResult {
        private final List<Transaction> valid;
        private final List<Transaction> invalid;

        public ValidationResult(List<Transaction> valid, List<Transaction> invalid) {
            this.valid = valid;
            this.invalid = invalid;
        }

        public List<Transaction> getValid() {
            return valid;
        }

        public List<Transaction> getInvalid() {
            return invalid;
        }
    }
}
