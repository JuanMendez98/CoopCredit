package com.crudzao.creditapp.application.services;

import com.crudzao.creditapp.domain.enums.AffiliateStatus;
import com.crudzao.creditapp.domain.enums.CreditRequestStatus;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.AffiliateEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.CreditRequestEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.AffiliateRepository;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.CreditRequestRepository;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.CreditRequestRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.CreditRequestResponse;
import com.crudzao.creditapp.infrastructure.exception.InvalidStateException;
import com.crudzao.creditapp.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing credit requests.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreditRequestService {

    private final CreditRequestRepository creditRequestRepository;
    private final AffiliateRepository affiliateRepository;

    /**
     * Create a new credit request.
     */
    public CreditRequestResponse createCreditRequest(CreditRequestRequest request) {
        log.info("Creating credit request for affiliate ID: {}, amount: {}", request.getAffiliateId(), request.getAmount());

        // Validate affiliate exists and is active
        AffiliateEntity affiliate = affiliateRepository.findById(request.getAffiliateId())
            .orElseThrow(() -> {
                log.warn("Affiliate not found with ID: {}", request.getAffiliateId());
                return new ResourceNotFoundException("Affiliate not found with ID: " + request.getAffiliateId());
            });

        if (affiliate.getStatus() != AffiliateStatus.ACTIVE) {
            log.warn("Attempt to create credit request for inactive affiliate ID: {}", request.getAffiliateId());
            throw new InvalidStateException("Affiliate is not active");
        }

        CreditRequestEntity creditRequest = new CreditRequestEntity();
        creditRequest.setAffiliate(affiliate);
        creditRequest.setAmount(request.getAmount());
        creditRequest.setTerm(request.getTerm());
        creditRequest.setRate(request.getRate());
        creditRequest.setRequestDate(LocalDateTime.now());
        creditRequest.setStatus(CreditRequestStatus.PENDING);

        CreditRequestEntity saved = creditRequestRepository.save(creditRequest);
        log.info("Credit request created successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    /**
     * Get credit request by ID.
     */
    @Transactional(readOnly = true)
    public CreditRequestResponse getCreditRequestById(Long id) {
        log.debug("Fetching credit request by ID: {}", id);
        CreditRequestEntity creditRequest = creditRequestRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Credit request not found with ID: {}", id);
                return new ResourceNotFoundException("Credit request not found with ID: " + id);
            });
        return mapToResponse(creditRequest);
    }

    /**
     * Get all credit requests for an affiliate.
     */
    @Transactional(readOnly = true)
    public List<CreditRequestResponse> getCreditRequestsByAffiliateId(Long affiliateId) {
        log.debug("Fetching credit requests for affiliate ID: {}", affiliateId);
        List<CreditRequestEntity> requests = creditRequestRepository.findByAffiliateId(affiliateId);
        return requests.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get credit requests by status.
     */
    @Transactional(readOnly = true)
    public List<CreditRequestResponse> getCreditRequestsByStatus(CreditRequestStatus status) {
        log.debug("Fetching credit requests with status: {}", status);
        List<CreditRequestEntity> requests = creditRequestRepository.findByStatus(status);
        return requests.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private CreditRequestResponse mapToResponse(CreditRequestEntity entity) {
        return new CreditRequestResponse(
            entity.getId(),
            entity.getAffiliate().getId(),
            entity.getAmount(),
            entity.getTerm(),
            entity.getRate(),
            entity.getRequestDate(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
