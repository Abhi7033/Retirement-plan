package com.blackrock.retirement.model;

/**
 * Represents a raw expense input from the user.
 */
public class Expense {

    private String timestamp;
    private double amount;

    public Expense() {
    }

    public Expense(String timestamp, double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
