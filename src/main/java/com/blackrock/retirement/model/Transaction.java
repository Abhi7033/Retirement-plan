package com.blackrock.retirement.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a processed transaction with rounding details.
 * Used across multiple endpoints with optional fields depending on context.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    private String date;
    private Double amount;
    private Double ceiling;
    private Double remanent;
    private Boolean inKPeriod;
    private String message;

    public Transaction() {
    }

    public Transaction(String date, double amount, double ceiling, double remanent) {
        this.date = date;
        this.amount = amount;
        this.ceiling = ceiling;
        this.remanent = remanent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCeiling() {
        return ceiling;
    }

    public void setCeiling(Double ceiling) {
        this.ceiling = ceiling;
    }

    public Double getRemanent() {
        return remanent;
    }

    public void setRemanent(Double remanent) {
        this.remanent = remanent;
    }

    public Boolean getInKPeriod() {
        return inKPeriod;
    }

    public void setInKPeriod(Boolean inKPeriod) {
        this.inKPeriod = inKPeriod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
