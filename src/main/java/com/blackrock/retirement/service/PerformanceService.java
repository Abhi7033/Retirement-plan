package com.blackrock.retirement.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class PerformanceService {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Reports current system performance metrics:
     * - Uptime formatted as a datetime string
     * - Heap memory usage in MB
     * - Active thread count
     */
    public String getUptime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = runtimeBean.getUptime();

        // format uptime as epoch-based datetime string to show duration
        LocalDateTime uptimeAsDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(uptimeMillis), ZoneOffset.UTC);

        return uptimeAsDateTime.format(TIME_FORMATTER);
    }

    /**
     * Returns heap memory usage in MB formatted as a string with 2 decimal places.
     */
    public String getMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedHeapBytes = memoryBean.getHeapMemoryUsage().getUsed();
        double usedHeapMB = usedHeapBytes / (1024.0 * 1024.0);

        return String.format("%.2f", usedHeapMB);
    }

    /**
     * Returns the current number of active threads.
     */
    public int getActiveThreadCount() {
        return Thread.activeCount();
    }
}
