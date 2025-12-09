package com.crudzao.creditapp.infrastructure.adapters.rest.dto;

import com.crudzao.creditapp.domain.enums.CreditRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Credit request response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestResponse {
    private Long id;
    private Long affiliateId;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal rate;
    private LocalDateTime requestDate;
    private CreditRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
