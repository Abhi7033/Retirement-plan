package com.blackrock.retirement.service;

import com.blackrock.retirement.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
public class TemporalFilterService {

    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    /**
     * Applies temporal constraints (q, p, k) to a list of transactions.
     * Processing order:
     *   1. Parse and validate each transaction (calculate ceiling/remanent)
     *   2. Apply q-period rules (fixed amount override)
     *   3. Apply p-period rules (extra amount addition)
     *   4. Check against k-periods (grouping)
     *
     * Also filters out invalid (negative amount, duplicates).
     */
    public FilterResult filterTransactions(List<Transaction> transactions,
                                           List<QPeriod> qPeriods,
                                           List<PPeriod> pPeriods,
                                           List<KPeriod> kPeriods,
                                           double wage) {

        List<Transaction> valid = new ArrayList<>();
        List<Transaction> invalid = new ArrayList<>();
        Set<String> seenDates = new LinkedHashSet<>();

        if (qPeriods == null) qPeriods = Collections.emptyList();
        if (pPeriods == null) pPeriods = Collections.emptyList();
        if (kPeriods == null) kPeriods = Collections.emptyList();

        for (Transaction txn : transactions) {
            String dateStr = txn.getDate();
            Double amount = txn.getAmount();

            // validate: negative amount
            if (amount != null && amount < 0) {
                Transaction inv = new Transaction();
                inv.setDate(dateStr);
                inv.setAmount(amount);
                inv.setMessage("Negative amounts are not allowed");
                invalid.add(inv);
                continue;
            }

            // validate: duplicate
            if (dateStr != null && seenDates.contains(dateStr)) {
                Transaction inv = new Transaction();
                inv.setDate(dateStr);
                inv.setAmount(amount);
                inv.setMessage("Duplicate transaction");
                invalid.add(inv);
                continue;
            }

            seenDates.add(dateStr);

            // step 1: calculate ceiling and remanent
            double amt = (amount != null) ? amount : 0;
            double ceiling = Math.ceil(amt / 100.0) * 100;
            double remanent = ceiling - amt;

            LocalDateTime txnDateTime = parseDateTime(dateStr);

            // step 2: apply q-period rules (fixed amount override)
            remanent = applyQPeriods(txnDateTime, remanent, qPeriods);

            // step 3: apply p-period rules (extra amount addition)
            remanent = applyPPeriods(txnDateTime, remanent, pPeriods);

            // step 4: check if in any k-period
            boolean inKPeriod = isInAnyKPeriod(txnDateTime, kPeriods);

            Transaction result = new Transaction();
            result.setDate(dateStr);
            result.setAmount(amt);
            result.setCeiling(ceiling);
            result.setRemanent(remanent);
            result.setInKPeriod(inKPeriod);
            valid.add(result);
        }

        return new FilterResult(valid, invalid);
    }

    /**
     * Applies q-period rules: if a transaction falls within a q-period,
     * the remanent is replaced with the fixed amount.
     * If multiple q-periods match, use the one with the latest start date.
     * If tied on start date, use the first in the list.
     */
    private double applyQPeriods(LocalDateTime txnDate, double remanent, List<QPeriod> qPeriods) {
        QPeriod bestMatch = null;
        LocalDateTime bestStart = null;
        int bestIndex = -1;

        for (int i = 0; i < qPeriods.size(); i++) {
            QPeriod q = qPeriods.get(i);
            LocalDateTime start = parseDateTime(q.getStart());
            LocalDateTime end = parseDateTime(q.getEnd());

            if (!txnDate.isBefore(start) && !txnDate.isAfter(end)) {
                if (bestMatch == null || start.isAfter(bestStart)
                        || (start.isEqual(bestStart) && i < bestIndex)) {
                    bestMatch = q;
                    bestStart = start;
                    bestIndex = i;
                }
            }
        }

        if (bestMatch != null) {
            return bestMatch.getFixed();
        }
        return remanent;
    }

    /**
     * Applies p-period rules: if a transaction falls within p-periods,
     * add all matching extras to the remanent. All matching p-periods are additive.
     */
    private double applyPPeriods(LocalDateTime txnDate, double remanent, List<PPeriod> pPeriods) {
        double totalExtra = 0;

        for (PPeriod p : pPeriods) {
            LocalDateTime start = parseDateTime(p.getStart());
            LocalDateTime end = parseDateTime(p.getEnd());

            if (!txnDate.isBefore(start) && !txnDate.isAfter(end)) {
                totalExtra += p.getExtra();
            }
        }

        return remanent + totalExtra;
    }

    /**
     * Checks if a transaction date falls within any k-period (inclusive).
     */
    private boolean isInAnyKPeriod(LocalDateTime txnDate, List<KPeriod> kPeriods) {
        for (KPeriod k : kPeriods) {
            LocalDateTime start = parseDateTime(k.getStart());
            LocalDateTime end = parseDateTime(k.getEnd());

            if (!txnDate.isBefore(start) && !txnDate.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, FLEXIBLE_FORMATTER);
    }

    /**
     * Holds the result of temporal filtering.
     */
    public static class FilterResult {
        private final List<Transaction> valid;
        private final List<Transaction> invalid;

        public FilterResult(List<Transaction> valid, List<Transaction> invalid) {
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
