package com.crudzaso.riskapp.service;

import com.crudzaso.riskapp.domain.RiskLevel;
import com.crudzaso.riskapp.dto.RiskEvaluationRequest;
import com.crudzaso.riskapp.dto.RiskEvaluationResponse;
import org.springframework.stereotype.Service;

/**
 * Service for evaluating credit risk based on document hash.
 * Generates a consistent score for each document.
 */
@Service
public class RiskEvaluationService {

    private static final int SCORE_MIN = 300;
    private static final int SCORE_MAX = 950;
    private static final int HASH_MOD = 1000;

    /**
     * Evaluates risk based on document, amount and term.
     * The score is always the same for the same document.
     *
     * @param request containing document, amount, term
     * @return RiskEvaluationResponse with score and risk level
     */
    public RiskEvaluationResponse evaluateRisk(RiskEvaluationRequest request) {
        String document = request.getDocument();

        // Generate consistent seed from document hash
        int seed = Math.abs(document.hashCode()) % HASH_MOD;

        // Generate score between 300-950 based on seed
        int score = SCORE_MIN + (seed * (SCORE_MAX - SCORE_MIN) / HASH_MOD);

        // Classify risk level based on score
        RiskLevel riskLevel = classifyRiskLevel(score);

        // Build response
        RiskEvaluationResponse response = new RiskEvaluationResponse();
        response.setDocument(document);
        response.setScore(score);
        response.setRiskLevel(riskLevel.name());
        response.setDetail(getDetailMessage(riskLevel));

        return response;
    }

    private RiskLevel classifyRiskLevel(int score) {
        if (score <= 500) {
            return RiskLevel.HIGH;
        } else if (score <= 700) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }

    private String getDetailMessage(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case HIGH -> "High credit risk. Limited approval probability.";
            case MEDIUM -> "Moderate credit history. Conditional approval possible.";
            case LOW -> "Low credit risk. Good approval probability.";
        };
    }
}
