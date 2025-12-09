package com.crudzao.creditapp.infrastructure.adapters.rest.dto;

import com.crudzao.creditapp.domain.enums.AffiliateStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Affiliate response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateResponse {
    private Long id;
    private String document;
    private String name;
    private BigDecimal salary;
    private LocalDateTime affiliationDate;
    private AffiliateStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
