package com.blackrock.retirement.controller;

import com.blackrock.retirement.dto.*;
import com.blackrock.retirement.model.Transaction;
import com.blackrock.retirement.service.SummaryService;
import com.blackrock.retirement.service.TemporalFilterService;
import com.blackrock.retirement.service.TransactionService;
import com.blackrock.retirement.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class TransactionController {

    private final TransactionService transactionService;
    private final ValidationService validationService;
    private final TemporalFilterService temporalFilterService;
    private final SummaryService summaryService;

    public TransactionController(TransactionService transactionService,
                                 ValidationService validationService,
                                 TemporalFilterService temporalFilterService,
                                 SummaryService summaryService) {
        this.transactionService = transactionService;
        this.validationService = validationService;
        this.temporalFilterService = temporalFilterService;
        this.summaryService = summaryService;
    }

    /**
     * POST /blackrock/challenge/v1/transactions:parse
     * Parses raw expenses and returns enriched transactions with ceiling and remanent.
     */
    @PostMapping("/transactions:parse")
    public ResponseEntity<ParseResponse> parseTransactions(@RequestBody ParseRequest request) {
        List<Transaction> transactions = transactionService.parseExpenses(request.getExpenses());
        return ResponseEntity.ok(new ParseResponse(transactions));
    }

    /**
     * POST /blackrock/challenge/v1/transactions:validator
     * Validates transactions - checks for negative amounts, duplicates, and constraint violations.
     */
    @PostMapping("/transactions:validator")
    public ResponseEntity<ValidatorResponse> validateTransactions(@RequestBody ValidatorRequest request) {
        ValidationService.ValidationResult result = validationService
                .validateTransactions(request.getWage(), request.getTransactions());

        return ResponseEntity.ok(new ValidatorResponse(result.getValid(), result.getInvalid()));
    }

    /**
     * POST /blackrock/challenge/v1/transactions:filter
     * Applies temporal constraints (q, p, k periods) to filter and adjust transactions.
     */
    @PostMapping("/transactions:filter")
    public ResponseEntity<ValidatorResponse> filterTransactions(@RequestBody FilterRequest request) {
        TemporalFilterService.FilterResult result = temporalFilterService
                .filterTransactions(
                        request.getTransactions(),
                        request.getQ(),
                        request.getP(),
                        request.getK(),
                        request.getWage()
                );

        return ResponseEntity.ok(new ValidatorResponse(result.getValid(), result.getInvalid()));
    }

    /**
     * POST /blackrock/challenge/v1/transactions:summary
     * Analyzes spending patterns and provides savings insights with investment readiness score.
     * Helps users understand their financial behavior before committing to a retirement plan.
     */
    @PostMapping("/transactions:summary")
    public ResponseEntity<SummaryResponse> transactionSummary(@RequestBody ValidatorRequest request) {
        SummaryResponse response = summaryService.analyzeSummary(request.getTransactions());
        return ResponseEntity.ok(response);
    }
}
