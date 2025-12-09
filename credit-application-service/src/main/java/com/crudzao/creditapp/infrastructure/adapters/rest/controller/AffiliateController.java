package com.crudzao.creditapp.infrastructure.adapters.rest.controller;

import com.crudzao.creditapp.application.services.AffiliateService;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.AffiliateRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.AffiliateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for affiliate operations.
 */
@RestController
@RequestMapping("/api/affiliates")
@RequiredArgsConstructor
public class AffiliateController {

    private final AffiliateService affiliateService;

    /**
     * Register a new affiliate.
     */
    @PostMapping
    public ResponseEntity<AffiliateResponse> registerAffiliate(@Valid @RequestBody AffiliateRequest request) {
        AffiliateResponse response = affiliateService.registerAffiliate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get affiliate by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AffiliateResponse> getAffiliateById(@PathVariable Long id) {
        AffiliateResponse response = affiliateService.getAffiliateById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get affiliate by document.
     */
    @GetMapping("/document/{document}")
    public ResponseEntity<AffiliateResponse> getAffiliateByDocument(@PathVariable String document) {
        AffiliateResponse response = affiliateService.getAffiliateByDocument(document);
        return ResponseEntity.ok(response);
    }
}
