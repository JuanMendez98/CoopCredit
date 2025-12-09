package com.crudzao.creditapp.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for risk evaluation response from risk-central-mock-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationResponse {
    private String document;
    private Integer score;
    private String riskLevel;
    private String detail;
}
