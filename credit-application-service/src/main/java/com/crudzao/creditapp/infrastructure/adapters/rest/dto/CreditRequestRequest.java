package com.crudzao.creditapp.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Credit request creation request DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestRequest {
    @NotNull(message = "Affiliate ID is required")
    private Long affiliateId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Term is required")
    @Positive(message = "Term must be greater than 0")
    private Integer term;

    @NotNull(message = "Rate is required")
    @Positive(message = "Rate must be greater than 0")
    private BigDecimal rate;
}
