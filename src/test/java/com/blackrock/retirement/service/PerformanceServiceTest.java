package com.blackrock.retirement.service;

// Test type: Unit Test
// Validation: Tests PerformanceService - uptime format, memory usage, thread count
// Command: mvn test -Dtest=PerformanceServiceTest

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceServiceTest {

    private PerformanceService service;

    @BeforeEach
    void setUp() {
        service = new PerformanceService();
    }

    @Test
    @DisplayName("Uptime should return a valid formatted datetime string")
    void testUptimeFormat() {
        String uptime = service.getUptime();
        assertNotNull(uptime);
        // format: yyyy-MM-dd HH:mm:ss.SSS
        assertTrue(uptime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}"),
                "Uptime should match datetime format: " + uptime);
    }

    @Test
    @DisplayName("Memory usage should return non-negative value in MB")
    void testMemoryUsage() {
        String memory = service.getMemoryUsage();
        assertNotNull(memory);
        double memoryMB = Double.parseDouble(memory);
        assertTrue(memoryMB > 0, "Memory usage should be positive");
    }

    @Test
    @DisplayName("Active thread count should be positive")
    void testActiveThreadCount() {
        int threads = service.getActiveThreadCount();
        assertTrue(threads > 0, "Should have at least one active thread");
    }

    @Test
    @DisplayName("Memory should be formatted with 2 decimal places")
    void testMemoryFormat() {
        String memory = service.getMemoryUsage();
        assertTrue(memory.matches("\\d+\\.\\d{2}"),
                "Memory should have exactly 2 decimal places: " + memory);
    }
}
