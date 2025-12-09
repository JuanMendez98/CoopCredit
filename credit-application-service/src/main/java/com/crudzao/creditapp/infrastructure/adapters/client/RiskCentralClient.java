package com.crudzao.creditapp.infrastructure.adapters.client;

import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RiskEvaluationRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RiskEvaluationResponse;
import com.crudzao.creditapp.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for communicating with risk-central-mock-service.
 * Makes REST calls to evaluate credit risk.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskCentralClient {

    private final RestTemplate restTemplate;

    @Value("${risk.service.url:http://localhost:8081/api/risk/risk-evaluation}")
    private String riskServiceUrl;

    /**
     * Evaluates credit risk for a given document, amount and term.
     *
     * @param request containing document, amount, term
     * @return RiskEvaluationResponse with score and risk level
     * @throws BusinessException if service call fails
     */
    public RiskEvaluationResponse evaluateRisk(RiskEvaluationRequest request) {
        log.info("Calling risk-central-mock-service for document: {}", request.getDocument());

        try {
            RiskEvaluationResponse response = restTemplate.postForObject(
                riskServiceUrl,
                request,
                RiskEvaluationResponse.class
            );

            if (response == null) {
                log.error("Risk service returned null response");
                throw new BusinessException("Risk evaluation service returned null response");
            }

            log.info("Risk evaluation completed for document: {}, score: {}, riskLevel: {}",
                response.getDocument(), response.getScore(), response.getRiskLevel());

            return response;
        } catch (RestClientException e) {
            log.error("Error calling risk-central-mock-service: {}", e.getMessage(), e);
            throw new BusinessException("Failed to evaluate risk: " + e.getMessage(), e);
        }
    }
}
