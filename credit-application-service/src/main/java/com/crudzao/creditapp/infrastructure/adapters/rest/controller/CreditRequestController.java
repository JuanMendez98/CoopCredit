package com.crudzao.creditapp.infrastructure.adapters.rest.controller;

import com.crudzao.creditapp.application.services.CreditRequestService;
import com.crudzao.creditapp.domain.enums.CreditRequestStatus;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.CreditRequestRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.CreditRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for credit request operations.
 */
@RestController
@RequestMapping("/api/credit-requests")
@RequiredArgsConstructor
public class CreditRequestController {

    private final CreditRequestService creditRequestService;

    /**
     * Create a new credit request.
     */
    @PostMapping
    public ResponseEntity<CreditRequestResponse> createCreditRequest(@Valid @RequestBody CreditRequestRequest request) {
        CreditRequestResponse response = creditRequestService.createCreditRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get credit request by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreditRequestResponse> getCreditRequestById(@PathVariable Long id) {
        CreditRequestResponse response = creditRequestService.getCreditRequestById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all credit requests for an affiliate.
     */
    @GetMapping("/affiliate/{affiliateId}")
    public ResponseEntity<List<CreditRequestResponse>> getCreditRequestsByAffiliateId(@PathVariable Long affiliateId) {
        List<CreditRequestResponse> responses = creditRequestService.getCreditRequestsByAffiliateId(affiliateId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get credit requests by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CreditRequestResponse>> getCreditRequestsByStatus(@PathVariable CreditRequestStatus status) {
        List<CreditRequestResponse> responses = creditRequestService.getCreditRequestsByStatus(status);
        return ResponseEntity.ok(responses);
    }
}
