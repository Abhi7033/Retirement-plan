package com.blackrock.retirement.controller;

import com.blackrock.retirement.dto.PerformanceResponse;
import com.blackrock.retirement.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    /**
     * GET /blackrock/challenge/v1/performance
     * Reports system execution metrics: uptime, memory usage, and active threads.
     */
    @GetMapping("/performance")
    public ResponseEntity<PerformanceResponse> getPerformance() {
        String time = performanceService.getUptime();
        String memory = performanceService.getMemoryUsage();
        int threads = performanceService.getActiveThreadCount();

        return ResponseEntity.ok(new PerformanceResponse(time, memory, threads));
    }
}
