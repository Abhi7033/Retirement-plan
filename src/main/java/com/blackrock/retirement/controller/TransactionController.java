package com.blackrock.retirement.controller;

import com.blackrock.retirement.dto.ParseRequest;
import com.blackrock.retirement.dto.ParseResponse;
import com.blackrock.retirement.dto.ValidatorRequest;
import com.blackrock.retirement.dto.ValidatorResponse;
import com.blackrock.retirement.model.Transaction;
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

    public TransactionController(TransactionService transactionService,
                                 ValidationService validationService) {
        this.transactionService = transactionService;
        this.validationService = validationService;
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
}
