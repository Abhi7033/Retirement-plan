package com.blackrock.retirement.model;

/**
 * Represents a q-period where a fixed investment amount overrides the calculated remanent.
 * During this period, the remanent is replaced with the fixed amount.
 */
public class QPeriod {

    private double fixed;
    private String start;
    private String end;

    public QPeriod() {
    }

    public QPeriod(double fixed, String start, String end) {
        this.fixed = fixed;
        this.start = start;
        this.end = end;
    }

    public double getFixed() {
        return fixed;
    }

    public void setFixed(double fixed) {
        this.fixed = fixed;
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
