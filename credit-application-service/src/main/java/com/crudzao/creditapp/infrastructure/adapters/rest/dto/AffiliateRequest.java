package com.crudzao.creditapp.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Affiliate creation/update request DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateRequest {
    @NotBlank(message = "Document is required")
    private String document;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be greater than 0")
    private BigDecimal salary;

    @NotNull(message = "Affiliation date is required")
    private LocalDateTime affiliationDate;
}
