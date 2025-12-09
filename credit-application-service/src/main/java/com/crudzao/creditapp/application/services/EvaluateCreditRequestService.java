package com.crudzao.creditapp.application.services;

import com.crudzao.creditapp.domain.enums.CreditDecision;
import com.crudzao.creditapp.domain.enums.CreditRequestStatus;
import com.crudzao.creditapp.domain.enums.RiskLevel;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.AffiliateEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.CreditRequestEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.RiskEvaluationEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.CreditRequestRepository;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.RiskEvaluationRepository;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RiskEvaluationResponse;
import com.crudzao.creditapp.infrastructure.exception.BusinessException;
import com.crudzao.creditapp.infrastructure.exception.InvalidStateException;
import com.crudzao.creditapp.infrastructure.exception.ResourceNotFoundException;
import com.crudzao.creditapp.application.ports.RiskCentralPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for evaluating credit requests. Orchestrates risk evaluation and credit decision making.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluateCreditRequestService {

    private final CreditRequestRepository creditRequestRepository;
    private final RiskEvaluationRepository riskEvaluationRepository;
    private final RiskCentralPort riskCentralPort;
    private final CreditPolicy creditPolicy;

    /**
     * Evaluates a credit request and makes approval/rejection decision. Process is transactional -
     * all or nothing.
     *
     * @param creditRequestId ID of credit request to evaluate
     * @return risk evaluation response with decision
     */
    @Transactional
    public RiskEvaluationResponse evaluateCreditRequest(Long creditRequestId) {
        log.info("Starting credit request evaluation for ID: {}", creditRequestId);

        // Get credit request
        CreditRequestEntity creditRequest = creditRequestRepository.findById(creditRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credit request not found with ID: " + creditRequestId));

        // Verify request status is PENDING
        if (creditRequest.getStatus() != CreditRequestStatus.PENDING) {
            throw new InvalidStateException(
                    "Credit request is not in PENDING status. Current status: "
                            + creditRequest.getStatus());
        }

        // Get affiliate
        AffiliateEntity affiliate = creditRequest.getAffiliate();
        if (affiliate == null) {
            throw new BusinessException("Affiliate not found for credit request");
        }

        // Verify affiliate is active
        if (!affiliate.getStatus()
                .equals(com.crudzao.creditapp.domain.enums.AffiliateStatus.ACTIVE)) {
            throw new BusinessException("Affiliate must be ACTIVE to evaluate credit request");
        }

        log.debug("Evaluating credit request: affiliateId={}, amount={}, term={}",
                affiliate.getId(), creditRequest.getAmount(), creditRequest.getTerm());

        // Call risk central service
        RiskEvaluationResponse riskEvaluation =
                riskCentralPort.evaluateRisk(affiliate.getDocument(),
                        creditRequest.getAmount().doubleValue(), creditRequest.getTerm());

        log.debug("Risk evaluation response: score={}, riskLevel={}", riskEvaluation.getScore(),
                riskEvaluation.getRiskLevel());

        // Convert risk level string to enum
        RiskLevel riskLevel = RiskLevel.valueOf(riskEvaluation.getRiskLevel());

        // Apply credit policies
        CreditPolicy.ValidationResult validationResult =
                creditPolicy.validateCreditRequest(affiliate, creditRequest.getAmount(),
                        creditRequest.getTerm(), riskEvaluation.getScore(), riskLevel);

        // Determine decision
        CreditDecision decision =
                validationResult.isApproved() ? CreditDecision.APPROVED : CreditDecision.REJECTED;
        CreditRequestStatus newStatus = validationResult.isApproved() ? CreditRequestStatus.APPROVED
                : CreditRequestStatus.REJECTED;

        log.info("Credit decision: {}, Reason: {}", decision, validationResult.getDetail());

        // Create risk evaluation entity
        RiskEvaluationEntity evaluation = new RiskEvaluationEntity();
        evaluation.setCreditRequest(creditRequest);
        evaluation.setScore(riskEvaluation.getScore());
        evaluation.setRiskLevel(riskLevel);
        evaluation.setDecision(decision);
        evaluation.setReason(validationResult.getDetail());
        evaluation.setEvaluationDate(LocalDateTime.now());

        // Save evaluation
        RiskEvaluationEntity savedEvaluation = riskEvaluationRepository.save(evaluation);
        log.debug("Risk evaluation saved with ID: {}", savedEvaluation.getId());

        // Update credit request status
        creditRequest.setStatus(newStatus);
        creditRequestRepository.save(creditRequest);
        log.debug("Credit request status updated to: {}", newStatus);

        // Prepare response
        RiskEvaluationResponse response = new RiskEvaluationResponse();
        response.setDocument(affiliate.getDocument());
        response.setScore(riskEvaluation.getScore());
        response.setRiskLevel(riskEvaluation.getRiskLevel());
        response.setDetail(validationResult.getDetail());

        log.info("Credit request evaluation completed. RequestId: {}, Decision: {}",
                creditRequestId, decision);

        return response;
    }
}
