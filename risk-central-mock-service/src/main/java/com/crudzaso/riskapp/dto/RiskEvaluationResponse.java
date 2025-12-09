package com.crudzaso.riskapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for risk evaluation containing score and risk level classification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationResponse {
    private String document;
    private int score;
    private String riskLevel;
    private String detail;
}
