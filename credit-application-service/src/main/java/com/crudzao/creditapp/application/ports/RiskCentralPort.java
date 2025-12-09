package com.crudzao.creditapp.application.ports;

import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RiskEvaluationResponse;

/**
 * Port for risk central service integration.
 */
public interface RiskCentralPort {

    /**
     * Evaluates credit risk for given document, amount and term.
     *
     * @param document customer document
     * @param amount credit amount requested
     * @param term credit term in months
     * @return risk evaluation with score and level
     */
    RiskEvaluationResponse evaluateRisk(String document, double amount, int term);
}
