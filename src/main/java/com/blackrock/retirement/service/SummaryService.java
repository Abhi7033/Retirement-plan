package com.blackrock.retirement.service;

import com.blackrock.retirement.dto.SummaryResponse;
import com.blackrock.retirement.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides spending pattern insights and savings potential analysis.
 * Helps users understand their financial behavior and readiness for retirement investing.
 */
@Service
public class SummaryService {

    /**
     * Analyzes a list of transactions to produce spending insights,
     * savings potential, and an investment readiness score.
     */
    public SummaryResponse analyzeSummary(List<Transaction> transactions) {
        SummaryResponse response = new SummaryResponse();

        if (transactions == null || transactions.isEmpty()) {
            response.setTotalTransactions(0);
            response.setValidTransactions(0);
            response.setInvalidTransactions(0);
            response.setInvestmentReadinessScore(0);
            response.setInvestmentReadinessLabel("No data");
            response.setTips(List.of("Start tracking your expenses to build a savings plan."));
            return response;
        }

        // separate valid and invalid
        List<Transaction> validTxns = new ArrayList<>();
        List<Transaction> invalidTxns = new ArrayList<>();
        Set<String> seenDates = new HashSet<>();

        for (Transaction txn : transactions) {
            boolean isInvalid = false;

            if (txn.getAmount() != null && txn.getAmount() < 0) {
                isInvalid = true;
            } else if (txn.getDate() != null && seenDates.contains(txn.getDate())) {
                isInvalid = true;
            } else if (txn.getAmount() != null && txn.getAmount() >= 500000) {
                isInvalid = true;
            }

            if (isInvalid) {
                invalidTxns.add(txn);
            } else {
                if (txn.getDate() != null) seenDates.add(txn.getDate());
                validTxns.add(txn);
            }
        }

        response.setTotalTransactions(transactions.size());
        response.setValidTransactions(validTxns.size());
        response.setInvalidTransactions(invalidTxns.size());

        if (validTxns.isEmpty()) {
            response.setInvestmentReadinessScore(0);
            response.setInvestmentReadinessLabel("Not ready");
            response.setTips(List.of("All your transactions are invalid. Check for negative amounts or duplicates."));
            return response;
        }

        // spending analysis on valid transactions
        double totalSpent = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        String highestDate = "";
        String lowestDate = "";
        double totalSavingsPotential = 0;

        for (Transaction txn : validTxns) {
            double amount = txn.getAmount() != null ? txn.getAmount() : 0;
            double ceiling = Math.ceil(amount / 100.0) * 100;
            double remanent = ceiling - amount;

            totalSpent += amount;
            totalSavingsPotential += remanent;

            if (amount > highest) {
                highest = amount;
                highestDate = txn.getDate();
            }
            if (amount < lowest) {
                lowest = amount;
                lowestDate = txn.getDate();
            }
        }

        double avgSpend = totalSpent / validTxns.size();
        double avgSavings = totalSavingsPotential / validTxns.size();

        // estimate monthly savings (assume ~30 transactions per month for daily expenses)
        double monthlySavings = roundToTwo(avgSavings * 30);
        double annualSavings = roundToTwo(monthlySavings * 12);

        response.setTotalSpent(roundToTwo(totalSpent));
        response.setAverageSpend(roundToTwo(avgSpend));
        response.setHighestSpend(roundToTwo(highest));
        response.setLowestSpend(roundToTwo(lowest));
        response.setHighestSpendDate(highestDate);
        response.setLowestSpendDate(lowestDate);
        response.setTotalSavingsPotential(roundToTwo(totalSavingsPotential));
        response.setAverageSavingsPerTransaction(roundToTwo(avgSavings));
        response.setMonthlySavingsEstimate(monthlySavings);
        response.setAnnualSavingsProjection(annualSavings);

        // calculate investment readiness score
        int score = calculateReadinessScore(validTxns, invalidTxns, avgSavings, totalSpent);
        response.setInvestmentReadinessScore(score);
        response.setInvestmentReadinessLabel(getReadinessLabel(score));

        // generate personalized tips
        response.setTips(generateTips(score, avgSavings, totalSpent, validTxns.size(), invalidTxns.size()));

        return response;
    }

    private int calculateReadinessScore(List<Transaction> valid, List<Transaction> invalid,
                                         double avgSavings, double totalSpent) {
        int score = 50; // base score

        // consistency bonus: more valid transactions = more consistent saver
        double validRatio = (double) valid.size() / (valid.size() + invalid.size());
        score += (int) (validRatio * 20);

        // savings ratio bonus
        double savingsRatio = (totalSpent > 0) ? avgSavings / (totalSpent / valid.size()) : 0;
        score += (int) (Math.min(savingsRatio, 0.5) * 40);

        // penalty for too few transactions (not enough data)
        if (valid.size() < 3) score -= 15;

        return Math.max(0, Math.min(100, score));
    }

    private String getReadinessLabel(int score) {
        if (score >= 80) return "Excellent - Ready to invest aggressively";
        if (score >= 60) return "Good - Can start regular investments";
        if (score >= 40) return "Moderate - Consider building an emergency fund first";
        if (score >= 20) return "Low - Focus on reducing expenses";
        return "Very Low - Need financial planning";
    }

    private List<String> generateTips(int score, double avgSavings, double totalSpent,
                                       int validCount, int invalidCount) {
        List<String> tips = new ArrayList<>();

        if (avgSavings < 20) {
            tips.add("Your average savings per transaction is low. Try rounding up your spends to save more spare change.");
        }

        if (invalidCount > 0) {
            tips.add("You have " + invalidCount + " invalid transactions. Review and fix them to maximize your investment pool.");
        }

        if (score >= 70) {
            tips.add("You're in great shape! Consider splitting investments between NPS (for tax benefits) and Index Funds (for higher growth).");
        }

        if (totalSpent > 10000 && avgSavings > 30) {
            tips.add("Your spending pattern generates good savings. Automate your investments to stay consistent.");
        }

        if (validCount >= 5) {
            tips.add("Consistent transaction history detected. You qualify for a disciplined savings plan.");
        }

        if (tips.isEmpty()) {
            tips.add("Start by tracking all your expenses. Every rupee saved is a rupee invested.");
        }

        return tips;
    }

    private double roundToTwo(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
