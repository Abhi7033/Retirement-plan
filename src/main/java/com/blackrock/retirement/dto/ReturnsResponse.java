package com.blackrock.retirement.dto;

import com.blackrock.retirement.model.SavingsByDate;
import java.util.List;

/**
 * Response body for the returns calculation endpoints.
 * Contains total amounts across valid transactions and per-period investment breakdown.
 */
public class ReturnsResponse {

    private double totalTransactionAmount;
    private double totalCeiling;
    private List<SavingsByDate> savingsByDates;

    public ReturnsResponse() {
    }

    public double getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(double totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    public double getTotalCeiling() {
        return totalCeiling;
    }

    public void setTotalCeiling(double totalCeiling) {
        this.totalCeiling = totalCeiling;
    }

    public List<SavingsByDate> getSavingsByDates() {
        return savingsByDates;
    }

    public void setSavingsByDates(List<SavingsByDate> savingsByDates) {
        this.savingsByDates = savingsByDates;
    }
}
