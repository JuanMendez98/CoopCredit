package com.crudzaso.riskapp.controller;

import com.crudzaso.riskapp.dto.RiskEvaluationRequest;
import com.crudzaso.riskapp.dto.RiskEvaluationResponse;
import com.crudzaso.riskapp.service.RiskEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for risk evaluation endpoints.
 */
@RestController
@RequestMapping("/api/risk")
public class RiskEvaluationController {

    private final RiskEvaluationService riskEvaluationService;

    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }

    /**
     * Evaluates credit risk for a given document, amount and term.
     *
     * @param request containing document, amount, term
     * @return RiskEvaluationResponse with score and risk level
     */
    @PostMapping("/risk-evaluation")
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(@RequestBody RiskEvaluationRequest request) {
        RiskEvaluationResponse response = riskEvaluationService.evaluateRisk(request);
        return ResponseEntity.ok(response);
    }
}
