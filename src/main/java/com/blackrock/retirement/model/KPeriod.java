package com.blackrock.retirement.model;

/**
 * Represents a k-period for grouping and evaluating investment totals.
 * Only transactions falling within k-period date ranges are considered for investment.
 */
public class KPeriod {

    private String start;
    private String end;

    public KPeriod() {
    }

    public KPeriod(String start, String end) {
        this.start = start;
        this.end = end;
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
}
