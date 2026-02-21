package com.blackrock.retirement.dto;

import com.blackrock.retirement.model.Transaction;
import java.util.List;

/**
 * Response body for the transaction validator endpoint.
 * Separates transactions into valid and invalid lists.
 */
public class ValidatorResponse {

    private List<Transaction> valid;
    private List<Transaction> invalid;

    public ValidatorResponse() {
    }

    public ValidatorResponse(List<Transaction> valid, List<Transaction> invalid) {
        this.valid = valid;
        this.invalid = invalid;
    }

    public List<Transaction> getValid() {
        return valid;
    }

    public void setValid(List<Transaction> valid) {
        this.valid = valid;
    }

    public List<Transaction> getInvalid() {
        return invalid;
    }

    public void setInvalid(List<Transaction> invalid) {
        this.invalid = invalid;
    }
}
