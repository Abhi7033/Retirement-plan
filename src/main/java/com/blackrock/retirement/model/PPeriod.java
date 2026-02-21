package com.blackrock.retirement.model;

/**
 * Represents a p-period where an extra amount is added to the remanent.
 * Multiple p-periods can overlap and all their extras are summed.
 */
public class PPeriod {

    private double extra;
    private String start;
    private String end;

    public PPeriod() {
    }

    public PPeriod(double extra, String start, String end) {
        this.extra = extra;
        this.start = start;
        this.end = end;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
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
