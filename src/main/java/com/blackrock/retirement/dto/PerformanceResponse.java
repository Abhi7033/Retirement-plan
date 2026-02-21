package com.blackrock.retirement.dto;

/**
 * Response body for the performance monitoring endpoint.
 * Reports system execution metrics.
 */
public class PerformanceResponse {

    private String time;
    private String memory;
    private int threads;

    public PerformanceResponse() {
    }

    public PerformanceResponse(String time, String memory, int threads) {
        this.time = time;
        this.memory = memory;
        this.threads = threads;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
