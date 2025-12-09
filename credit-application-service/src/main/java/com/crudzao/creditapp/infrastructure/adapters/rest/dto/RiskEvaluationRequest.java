package com.crudzao.creditapp.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for communicating with risk-central-mock-service.
 * Request to evaluate credit risk.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationRequest {
    private String document;
    private BigDecimal amount;
    private Integer term;
}
