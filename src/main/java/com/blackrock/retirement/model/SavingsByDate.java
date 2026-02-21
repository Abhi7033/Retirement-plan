package com.blackrock.retirement.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents the savings breakdown for a single k-period range.
 * Contains the date range, total amount saved, investment profits, and tax benefit.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SavingsByDate {

    private String start;
    private String end;
    private Double amount;
    private Double profit;
    private Double taxBenefit;

    public SavingsByDate() {
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getTaxBenefit() {
        return taxBenefit;
    }

    public void setTaxBenefit(Double taxBenefit) {
        this.taxBenefit = taxBenefit;
    }
}
