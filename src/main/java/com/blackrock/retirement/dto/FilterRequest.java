package com.blackrock.retirement.dto;

import com.blackrock.retirement.model.KPeriod;
import com.blackrock.retirement.model.PPeriod;
import com.blackrock.retirement.model.QPeriod;
import com.blackrock.retirement.model.Transaction;

import java.util.List;

/**
 * Request body for the temporal constraints filter endpoint.
 */
public class FilterRequest {

    private List<QPeriod> q;
    private List<PPeriod> p;
    private List<KPeriod> k;
    private double wage;
    private List<Transaction> transactions;

    public FilterRequest() {
    }

    public List<QPeriod> getQ() {
        return q;
    }

    public void setQ(List<QPeriod> q) {
        this.q = q;
    }

    public List<PPeriod> getP() {
        return p;
    }

    public void setP(List<PPeriod> p) {
        this.p = p;
    }

    public List<KPeriod> getK() {
        return k;
    }

    public void setK(List<KPeriod> k) {
        this.k = k;
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
