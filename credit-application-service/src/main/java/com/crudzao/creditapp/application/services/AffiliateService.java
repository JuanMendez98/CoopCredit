package com.crudzao.creditapp.application.services;

import com.crudzao.creditapp.domain.enums.AffiliateStatus;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.AffiliateEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.AffiliateRepository;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.AffiliateRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.AffiliateResponse;
import com.crudzao.creditapp.infrastructure.exception.BusinessException;
import com.crudzao.creditapp.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing affiliates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AffiliateService {

    private final AffiliateRepository affiliateRepository;

    /**
     * Register a new affiliate.
     */
    public AffiliateResponse registerAffiliate(AffiliateRequest request) {
        log.info("Registering new affiliate with document: {}", request.getDocument());

        // Check if document already exists
        if (affiliateRepository.findByDocument(request.getDocument()).isPresent()) {
            log.warn("Attempt to register affiliate with existing document: {}", request.getDocument());
            throw new BusinessException("Affiliate with document " + request.getDocument() + " already exists");
        }

        AffiliateEntity affiliate = new AffiliateEntity();
        affiliate.setDocument(request.getDocument());
        affiliate.setName(request.getName());
        affiliate.setSalary(request.getSalary());
        affiliate.setAffiliationDate(request.getAffiliationDate());
        affiliate.setStatus(AffiliateStatus.ACTIVE);

        AffiliateEntity saved = affiliateRepository.save(affiliate);
        log.info("Affiliate registered successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    /**
     * Get affiliate by ID.
     */
    @Transactional(readOnly = true)
    public AffiliateResponse getAffiliateById(Long id) {
        log.debug("Fetching affiliate by ID: {}", id);
        AffiliateEntity affiliate = affiliateRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Affiliate not found with ID: {}", id);
                return new ResourceNotFoundException("Affiliate not found with ID: " + id);
            });
        return mapToResponse(affiliate);
    }

    /**
     * Get affiliate by document.
     */
    @Transactional(readOnly = true)
    public AffiliateResponse getAffiliateByDocument(String document) {
        log.debug("Fetching affiliate by document: {}", document);
        AffiliateEntity affiliate = affiliateRepository.findByDocument(document)
            .orElseThrow(() -> {
                log.warn("Affiliate not found with document: {}", document);
                return new ResourceNotFoundException("Affiliate not found with document: " + document);
            });
        return mapToResponse(affiliate);
    }

    private AffiliateResponse mapToResponse(AffiliateEntity entity) {
        return new AffiliateResponse(
            entity.getId(),
            entity.getDocument(),
            entity.getName(),
            entity.getSalary(),
            entity.getAffiliationDate(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
