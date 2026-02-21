package com.blackrock.retirement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Response for the /transactions:summary endpoint.
 * Provides spending pattern insights and savings potential analysis
 * to help users understand their financial behavior.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryResponse {

    private int totalTransactions;
    private int validTransactions;
    private int invalidTransactions;

    // spending analysis
    private double totalSpent;
    private double averageSpend;
    private double highestSpend;
    private double lowestSpend;
    private String highestSpendDate;
    private String lowestSpendDate;

    // savings potential
    private double totalSavingsPotential;
    private double averageSavingsPerTransaction;
    private double monthlySavingsEstimate;
    private double annualSavingsProjection;

    // investment readiness score (0-100)
    private int investmentReadinessScore;
    private String investmentReadinessLabel;
    private List<String> tips;

    public SummaryResponse() {
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getValidTransactions() {
        return validTransactions;
    }

    public void setValidTransactions(int validTransactions) {
        this.validTransactions = validTransactions;
    }

    public int getInvalidTransactions() {
        return invalidTransactions;
    }

    public void setInvalidTransactions(int invalidTransactions) {
        this.invalidTransactions = invalidTransactions;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getAverageSpend() {
        return averageSpend;
    }

    public void setAverageSpend(double averageSpend) {
        this.averageSpend = averageSpend;
    }

    public double getHighestSpend() {
        return highestSpend;
    }

    public void setHighestSpend(double highestSpend) {
        this.highestSpend = highestSpend;
    }

    public double getLowestSpend() {
        return lowestSpend;
    }

    public void setLowestSpend(double lowestSpend) {
        this.lowestSpend = lowestSpend;
    }

    public String getHighestSpendDate() {
        return highestSpendDate;
    }

    public void setHighestSpendDate(String highestSpendDate) {
        this.highestSpendDate = highestSpendDate;
    }

    public String getLowestSpendDate() {
        return lowestSpendDate;
    }

    public void setLowestSpendDate(String lowestSpendDate) {
        this.lowestSpendDate = lowestSpendDate;
    }

    public double getTotalSavingsPotential() {
        return totalSavingsPotential;
    }

    public void setTotalSavingsPotential(double totalSavingsPotential) {
        this.totalSavingsPotential = totalSavingsPotential;
    }

    public double getAverageSavingsPerTransaction() {
        return averageSavingsPerTransaction;
    }

    public void setAverageSavingsPerTransaction(double averageSavingsPerTransaction) {
        this.averageSavingsPerTransaction = averageSavingsPerTransaction;
    }

    public double getMonthlySavingsEstimate() {
        return monthlySavingsEstimate;
    }

    public void setMonthlySavingsEstimate(double monthlySavingsEstimate) {
        this.monthlySavingsEstimate = monthlySavingsEstimate;
    }

    public double getAnnualSavingsProjection() {
        return annualSavingsProjection;
    }

    public void setAnnualSavingsProjection(double annualSavingsProjection) {
        this.annualSavingsProjection = annualSavingsProjection;
    }

    public int getInvestmentReadinessScore() {
        return investmentReadinessScore;
    }

    public void setInvestmentReadinessScore(int investmentReadinessScore) {
        this.investmentReadinessScore = investmentReadinessScore;
    }

    public String getInvestmentReadinessLabel() {
        return investmentReadinessLabel;
    }

    public void setInvestmentReadinessLabel(String investmentReadinessLabel) {
        this.investmentReadinessLabel = investmentReadinessLabel;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }
}
