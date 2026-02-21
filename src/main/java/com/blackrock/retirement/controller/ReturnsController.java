package com.blackrock.retirement.controller;

import com.blackrock.retirement.dto.CompareResponse;
import com.blackrock.retirement.dto.ReturnsRequest;
import com.blackrock.retirement.dto.ReturnsResponse;
import com.blackrock.retirement.service.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class ReturnsController {

    private final InvestmentService investmentService;

    public ReturnsController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    /**
     * POST /blackrock/challenge/v1/returns:nps
     * Calculates NPS returns with compound interest, inflation adjustment, and tax benefit.
     */
    @PostMapping("/returns:nps")
    public ResponseEntity<ReturnsResponse> calculateNpsReturns(@RequestBody ReturnsRequest request) {
        ReturnsResponse response = investmentService.calculateNpsReturns(
                request.getAge(),
                request.getWage(),
                request.getInflation(),
                request.getQ(),
                request.getP(),
                request.getK(),
                request.getTransactions()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /blackrock/challenge/v1/returns:index
     * Calculates Index Fund (NIFTY 50) returns with compound interest and inflation adjustment.
     */
    @PostMapping("/returns:index")
    public ResponseEntity<ReturnsResponse> calculateIndexReturns(@RequestBody ReturnsRequest request) {
        ReturnsResponse response = investmentService.calculateIndexReturns(
                request.getAge(),
                request.getWage(),
                request.getInflation(),
                request.getQ(),
                request.getP(),
                request.getK(),
                request.getTransactions()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /blackrock/challenge/v1/returns:compare
     * Side-by-side NPS vs Index Fund comparison with personalized recommendation.
     * Helps users choose the right investment strategy based on their age, income, and risk profile.
     */
    @PostMapping("/returns:compare")
    public ResponseEntity<CompareResponse> compareReturns(@RequestBody ReturnsRequest request) {
        CompareResponse response = investmentService.compareReturns(
                request.getAge(),
                request.getWage(),
                request.getInflation(),
                request.getQ(),
                request.getP(),
                request.getK(),
                request.getTransactions()
        );
        return ResponseEntity.ok(response);
    }
}
