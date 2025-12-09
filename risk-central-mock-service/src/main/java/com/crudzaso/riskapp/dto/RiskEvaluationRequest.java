package com.crudzaso.riskapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for risk evaluation containing document, amount and term.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationRequest {
    private String document;
    private double amount;
    private int term;
}
