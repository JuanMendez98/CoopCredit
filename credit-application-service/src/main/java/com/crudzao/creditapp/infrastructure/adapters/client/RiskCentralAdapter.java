package com.crudzao.creditapp.infrastructure.adapters.client;

import com.crudzao.creditapp.application.ports.RiskCentralPort;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RiskEvaluationRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RiskEvaluationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter for risk central service integration.
 * Implements RiskCentralPort using RiskCentralClient.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskCentralAdapter implements RiskCentralPort {

    private final RiskCentralClient riskCentralClient;

    @Override
    public RiskEvaluationResponse evaluateRisk(String document, double amount, int term) {
        log.info("Evaluating risk for document: {}, amount: {}, term: {} months", document, amount, term);

        RiskEvaluationRequest request = new RiskEvaluationRequest();
        request.setDocument(document);
        request.setAmount(java.math.BigDecimal.valueOf(amount));
        request.setTerm(term);

        RiskEvaluationResponse response = riskCentralClient.evaluateRisk(request);

        log.info("Risk evaluation completed. Document: {}, Score: {}, RiskLevel: {}",
                document, response.getScore(), response.getRiskLevel());

        return response;
    }
}
