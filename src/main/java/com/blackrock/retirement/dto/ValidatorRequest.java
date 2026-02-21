package com.blackrock.retirement.dto;

import com.blackrock.retirement.model.Transaction;
import java.util.List;

/**
 * Request body for the transaction validator endpoint.
 */
public class ValidatorRequest {

    private double wage;
    private List<Transaction> transactions;

    public ValidatorRequest() {
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
