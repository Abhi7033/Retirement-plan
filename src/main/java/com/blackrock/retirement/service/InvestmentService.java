package com.blackrock.retirement.service;

import com.blackrock.retirement.dto.CompareResponse;
import com.blackrock.retirement.dto.ReturnsResponse;
import com.blackrock.retirement.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
public class InvestmentService {

    private static final double NPS_RATE = 0.0711;
    private static final double INDEX_RATE = 0.1449;
    private static final int RETIREMENT_AGE = 60;
    private static final int MINIMUM_INVESTMENT_YEARS = 5;

    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    /**
     * Calculates returns for NPS (National Pension Scheme).
     * Includes tax benefit computation.
     */
    public ReturnsResponse calculateNpsReturns(int age, double monthlyWage, double inflation,
                                                List<QPeriod> qPeriods, List<PPeriod> pPeriods,
                                                List<KPeriod> kPeriods,
                                                List<Transaction> transactions) {
        return calculateReturns(age, monthlyWage, inflation, qPeriods, pPeriods,
                kPeriods, transactions, NPS_RATE, true);
    }

    /**
     * Calculates returns for Index Fund (NIFTY 50).
     * No tax benefit applied.
     */
    public ReturnsResponse calculateIndexReturns(int age, double monthlyWage, double inflation,
                                                  List<QPeriod> qPeriods, List<PPeriod> pPeriods,
                                                  List<KPeriod> kPeriods,
                                                  List<Transaction> transactions) {
        return calculateReturns(age, monthlyWage, inflation, qPeriods, pPeriods,
                kPeriods, transactions, INDEX_RATE, false);
    }

    /**
     * Compares NPS vs Index Fund returns side-by-side.
     * Provides a recommendation based on age, income, and risk profile.
     * Helps users make an informed decision about retirement investment strategy.
     */
    public CompareResponse compareReturns(int age, double monthlyWage, double inflation,
                                           List<QPeriod> qPeriods, List<PPeriod> pPeriods,
                                           List<KPeriod> kPeriods,
                                           List<Transaction> transactions) {

        ReturnsResponse npsResult = calculateNpsReturns(age, monthlyWage, inflation,
                qPeriods, pPeriods, kPeriods, transactions);
        ReturnsResponse indexResult = calculateIndexReturns(age, monthlyWage, inflation,
                qPeriods, pPeriods, kPeriods, transactions);

        CompareResponse response = new CompareResponse();
        response.setTotalTransactionAmount(npsResult.getTotalTransactionAmount());
        response.setTotalCeiling(npsResult.getTotalCeiling());

        // calculate total investable (sum of remanent across k-periods)
        double totalInvestable = 0;
        if (npsResult.getSavingsByDates() != null) {
            for (SavingsByDate s : npsResult.getSavingsByDates()) {
                totalInvestable += (s.getAmount() != null) ? s.getAmount() : 0;
            }
        }
        response.setTotalInvestable(roundToTwo(totalInvestable));

        // NPS totals
        response.setNpsSavings(npsResult.getSavingsByDates());
        double npsTotalProfit = 0;
        double npsTotalTaxBenefit = 0;
        if (npsResult.getSavingsByDates() != null) {
            for (SavingsByDate s : npsResult.getSavingsByDates()) {
                npsTotalProfit += (s.getProfit() != null) ? s.getProfit() : 0;
                npsTotalTaxBenefit += (s.getTaxBenefit() != null) ? s.getTaxBenefit() : 0;
            }
        }
        response.setNpsTotalProfit(roundToTwo(npsTotalProfit));
        response.setNpsTotalTaxBenefit(roundToTwo(npsTotalTaxBenefit));
        response.setNpsEffectiveGain(roundToTwo(npsTotalProfit + npsTotalTaxBenefit));

        // Index totals
        response.setIndexSavings(indexResult.getSavingsByDates());
        double indexTotalProfit = 0;
        if (indexResult.getSavingsByDates() != null) {
            for (SavingsByDate s : indexResult.getSavingsByDates()) {
                indexTotalProfit += (s.getProfit() != null) ? s.getProfit() : 0;
            }
        }
        response.setIndexTotalProfit(roundToTwo(indexTotalProfit));
        response.setIndexEffectiveGain(roundToTwo(indexTotalProfit));

        // generate recommendation
        generateRecommendation(response, age, monthlyWage, npsTotalProfit, npsTotalTaxBenefit, indexTotalProfit);

        return response;
    }

    /**
     * Generates a personalized recommendation based on the user's profile.
     * Considers age (risk tolerance), income (tax bracket), and return comparison.
     */
    private void generateRecommendation(CompareResponse response, int age, double monthlyWage,
                                         double npsProfit, double npsTaxBenefit, double indexProfit) {

        double annualIncome = monthlyWage * 12;
        double npsEffective = npsProfit + npsTaxBenefit;
        double indexEffective = indexProfit;

        int npsPercent;
        int indexPercent;
        String riskProfile;
        String reasoning;

        if (age < 35) {
            // young: higher risk tolerance, index-heavy
            riskProfile = "Aggressive";
            indexPercent = 70;
            npsPercent = 30;
            reasoning = "At " + age + ", you have " + (60 - age) + " years till retirement. "
                    + "Your long time horizon allows for higher equity exposure through Index Funds (14.49% avg return). "
                    + "NPS provides stable 7.11% returns with tax savings under Section 80CCD.";
        } else if (age < 45) {
            // mid-career: balanced approach
            riskProfile = "Moderate";
            indexPercent = 50;
            npsPercent = 50;
            reasoning = "At " + age + ", a balanced approach works best. "
                    + "Split equally between NPS (guaranteed returns + tax deduction up to ₹2L) "
                    + "and Index Funds (higher growth potential). Adjust as you approach 50.";
        } else if (age < 55) {
            // approaching retirement: conservative tilt
            riskProfile = "Conservative";
            indexPercent = 30;
            npsPercent = 70;
            reasoning = "At " + age + ", capital preservation becomes important. "
                    + "NPS offers stability and tax benefits. Keep some Index Fund exposure for inflation-beating returns.";
        } else {
            // near retirement: safety first
            riskProfile = "Very Conservative";
            indexPercent = 20;
            npsPercent = 80;
            reasoning = "At " + age + ", you're close to retirement. Prioritize NPS for guaranteed returns "
                    + "and maximum tax benefits. Minimal Index Fund allocation for liquidity.";
        }

        // adjust based on income bracket (high earners benefit more from NPS tax deduction)
        if (annualIncome > 1500000) {
            npsPercent = Math.min(npsPercent + 10, 90);
            indexPercent = 100 - npsPercent;
            reasoning += " Your income is in the 30% tax bracket - NPS tax deduction is highly valuable.";
        }

        // final recommendation
        String recommendation;
        if (npsEffective > indexEffective) {
            recommendation = "NPS is more beneficial for your profile (effective gain: ₹"
                    + roundToTwo(npsEffective) + " vs ₹" + roundToTwo(indexEffective)
                    + "). The tax benefit makes NPS the winner despite lower market returns.";
        } else {
            recommendation = "Index Fund generates higher returns for your profile (₹"
                    + roundToTwo(indexEffective) + " vs ₹" + roundToTwo(npsEffective)
                    + "). However, consider NPS allocation for tax savings.";
        }

        response.setRecommendation(recommendation);
        response.setRiskProfile(riskProfile);
        response.setSuggestedNpsPercent(npsPercent);
        response.setSuggestedIndexPercent(indexPercent);
        response.setReasoning(reasoning);
    }

    private ReturnsResponse calculateReturns(int age, double monthlyWage, double inflation,
                                              List<QPeriod> qPeriods, List<PPeriod> pPeriods,
                                              List<KPeriod> kPeriods,
                                              List<Transaction> transactions,
                                              double annualRate, boolean isNps) {

        if (qPeriods == null) qPeriods = Collections.emptyList();
        if (pPeriods == null) pPeriods = Collections.emptyList();
        if (kPeriods == null) kPeriods = Collections.emptyList();

        double annualIncome = monthlyWage * 12;
        int yearsToRetirement = (age < RETIREMENT_AGE)
                ? (RETIREMENT_AGE - age)
                : MINIMUM_INVESTMENT_YEARS;

        // step 1: process each transaction - calculate ceiling, remanent, apply q and p
        List<ProcessedTransaction> processed = processTransactions(transactions, qPeriods, pPeriods);

        // filter out invalid transactions (negative amount, duplicates)
        List<ProcessedTransaction> validProcessed = filterValidTransactions(processed);

        // compute totals from valid transactions
        double totalAmount = 0;
        double totalCeiling = 0;
        for (ProcessedTransaction pt : validProcessed) {
            totalAmount += pt.amount;
            totalCeiling += pt.ceiling;
        }

        // step 4: group by k-periods and calculate returns
        List<SavingsByDate> savingsByDates = new ArrayList<>();

        for (KPeriod kPeriod : kPeriods) {
            LocalDateTime kStart = parseDateTime(kPeriod.getStart());
            LocalDateTime kEnd = parseDateTime(kPeriod.getEnd());

            // sum up remanent for transactions in this k-period
            double periodAmount = 0;
            for (ProcessedTransaction pt : validProcessed) {
                LocalDateTime txnDate = parseDateTime(pt.date);
                if (!txnDate.isBefore(kStart) && !txnDate.isAfter(kEnd)) {
                    periodAmount += pt.remanent;
                }
            }

            // step 5: calculate compound interest
            double futureValue = periodAmount * Math.pow(1 + annualRate, yearsToRetirement);

            // adjust for inflation
            double inflationRate = inflation / 100.0;
            double realValue = futureValue / Math.pow(1 + inflationRate, yearsToRetirement);

            // profit is the gain over the principal (inflation-adjusted return minus invested amount)
            double profit = roundToTwo(realValue - periodAmount);

            // calculate tax benefit for NPS
            double taxBenefit = 0;
            if (isNps) {
                taxBenefit = calculateTaxBenefit(periodAmount, annualIncome);
            }

            SavingsByDate saving = new SavingsByDate();
            saving.setStart(kPeriod.getStart());
            saving.setEnd(kPeriod.getEnd());
            saving.setAmount(roundToTwo(periodAmount));
            saving.setProfit(profit);
            saving.setTaxBenefit(roundToTwo(taxBenefit));

            savingsByDates.add(saving);
        }

        ReturnsResponse response = new ReturnsResponse();
        response.setTotalTransactionAmount(roundToTwo(totalAmount));
        response.setTotalCeiling(roundToTwo(totalCeiling));
        response.setSavingsByDates(savingsByDates);

        return response;
    }

    /**
     * Process raw transactions: compute ceiling, remanent, apply q and p rules.
     */
    private List<ProcessedTransaction> processTransactions(List<Transaction> transactions,
                                                            List<QPeriod> qPeriods,
                                                            List<PPeriod> pPeriods) {
        List<ProcessedTransaction> result = new ArrayList<>();

        for (Transaction txn : transactions) {
            ProcessedTransaction pt = new ProcessedTransaction();
            pt.date = txn.getDate();
            pt.amount = (txn.getAmount() != null) ? txn.getAmount() : 0;
            pt.ceiling = Math.ceil(pt.amount / 100.0) * 100;
            pt.remanent = pt.ceiling - pt.amount;
            pt.isValid = true;
            pt.isDuplicate = false;

            // check for negative amounts
            if (pt.amount < 0) {
                pt.isValid = false;
                result.add(pt);
                continue;
            }

            LocalDateTime txnDate = parseDateTime(pt.date);

            // apply q-period rules
            pt.remanent = applyQPeriods(txnDate, pt.remanent, qPeriods);

            // apply p-period rules
            pt.remanent = applyPPeriods(txnDate, pt.remanent, pPeriods);

            result.add(pt);
        }

        return result;
    }

    /**
     * Filter out invalid (negative) and duplicate transactions.
     */
    private List<ProcessedTransaction> filterValidTransactions(List<ProcessedTransaction> transactions) {
        List<ProcessedTransaction> valid = new ArrayList<>();
        Set<String> seenDates = new HashSet<>();

        for (ProcessedTransaction pt : transactions) {
            if (!pt.isValid) continue;
            if (pt.date != null && seenDates.contains(pt.date)) continue;

            seenDates.add(pt.date);
            valid.add(pt);
        }

        return valid;
    }

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
     * Calculates the tax benefit from NPS investment.
     * Uses simplified Indian tax slabs.
     *
     * Tax slabs:
     *   0 to 7,00,000: 0%
     *   7,00,001 to 10,00,000: 10%
     *   10,00,001 to 12,00,000: 15%
     *   12,00,001 to 15,00,000: 20%
     *   Above 15,00,000: 30%
     */
    private double calculateTaxBenefit(double invested, double annualIncome) {
        // NPS deduction is min of: invested, 10% of annual income, 2,00,000
        double npsDeduction = Math.min(invested, Math.min(0.10 * annualIncome, 200000));

        double taxWithoutDeduction = calculateTax(annualIncome);
        double taxWithDeduction = calculateTax(annualIncome - npsDeduction);

        return taxWithoutDeduction - taxWithDeduction;
    }

    /**
     * Calculates income tax using simplified slabs.
     */
    private double calculateTax(double income) {
        if (income <= 0) return 0;

        double tax = 0;

        if (income > 1500000) {
            tax += (income - 1500000) * 0.30;
            income = 1500000;
        }
        if (income > 1200000) {
            tax += (income - 1200000) * 0.20;
            income = 1200000;
        }
        if (income > 1000000) {
            tax += (income - 1000000) * 0.15;
            income = 1000000;
        }
        if (income > 700000) {
            tax += (income - 700000) * 0.10;
        }

        return tax;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, FLEXIBLE_FORMATTER);
    }

    private double roundToTwo(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Internal representation of a processed transaction.
     */
    private static class ProcessedTransaction {
        String date;
        double amount;
        double ceiling;
        double remanent;
        boolean isValid;
        boolean isDuplicate;
    }
}
