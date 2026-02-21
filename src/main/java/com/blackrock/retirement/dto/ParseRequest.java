package com.blackrock.retirement.dto;

import com.blackrock.retirement.model.Expense;
import java.util.List;

/**
 * Request body for the transaction parse endpoint.
 */
public class ParseRequest {

    private List<Expense> expenses;

    public ParseRequest() {
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
