package com.blackrock.retirement.dto;

import com.blackrock.retirement.model.Transaction;
import java.util.List;

/**
 * Response body for the transaction parse endpoint.
 */
public class ParseResponse {

    private List<Transaction> transactions;

    public ParseResponse() {
    }

    public ParseResponse(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
