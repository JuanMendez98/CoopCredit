package com.crudzao.creditapp.infrastructure.adapters.rest.validator;

import com.crudzao.creditapp.domain.enums.AffiliateStatus;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.AffiliateRepository;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.AffiliateEntity;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.CreditRequestRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Validator for cross-field validation in credit requests.
 * Validates:
 * - Affiliate is ACTIVE
 * - Affiliate has at least 6 months affiliation
 * - Monthly payment <= 40% of salary
 * - Amount <= 5x salary
 * - Term is between 1 and 360 months
 */
@Slf4j
@Component
public class CreditRequestValidator implements ConstraintValidator<ValidCreditRequest, CreditRequestRequest> {

    @Autowired
    private AffiliateRepository affiliateRepository;

    @Override
    public void initialize(ValidCreditRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreditRequestRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        log.info("Validating credit request for affiliate ID: {}", value.getAffiliateId());

        Optional<AffiliateEntity> affiliate = affiliateRepository.findById(value.getAffiliateId());

        if (affiliate.isEmpty()) {
            log.warn("Affiliate not found with ID: {}", value.getAffiliateId());
            addConstraintViolation(context, "Affiliate not found");
            return false;
        }

        AffiliateEntity affiliateEntity = affiliate.get();
        context.disableDefaultConstraintViolation();

        boolean isValid = true;

        // Validate affiliate is ACTIVE
        log.info("Affiliate status: {} (expected: {})", affiliateEntity.getStatus(), AffiliateStatus.ACTIVE);
        if (affiliateEntity.getStatus() != AffiliateStatus.ACTIVE) {
            log.warn("Affiliate is not ACTIVE. Status: {}", affiliateEntity.getStatus());
            addConstraintViolation(context, "Affiliate must be ACTIVE to request credit");
            isValid = false;
        }

        // Validate minimum affiliation period (6 months)
        long monthsSinceAffiliation = ChronoUnit.MONTHS.between(
                affiliateEntity.getAffiliationDate(),
                LocalDateTime.now()
        );

        if (monthsSinceAffiliation < 6) {
            addConstraintViolation(context,
                    String.format("Affiliate must have at least 6 months of affiliation. Current: %d months",
                            monthsSinceAffiliation));
            isValid = false;
        }

        // Validate amount <= 5x salary
        BigDecimal maxAmount = affiliateEntity.getSalary().multiply(BigDecimal.valueOf(5));
        if (value.getAmount().compareTo(maxAmount) > 0) {
            addConstraintViolation(context,
                    String.format("Amount cannot exceed 5x salary (max: %s)", maxAmount));
            isValid = false;
        }

        // Validate term is valid (1-360 months)
        if (value.getTerm() == null || value.getTerm() <= 0 || value.getTerm() > 360) {
            addConstraintViolation(context, "Term must be between 1 and 360 months");
            isValid = false;
        } else {
            // Validate monthly payment <= 40% of salary
            BigDecimal monthlyPayment = value.getAmount()
                    .divide(BigDecimal.valueOf(value.getTerm()), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(1 + value.getRate().doubleValue() / 100));

            BigDecimal maxMonthlyPayment = affiliateEntity.getSalary().multiply(BigDecimal.valueOf(0.4));

            if (monthlyPayment.compareTo(maxMonthlyPayment) > 0) {
                addConstraintViolation(context,
                        String.format("Monthly payment (%.2f) exceeds 40%% of salary (%.2f)",
                                monthlyPayment, maxMonthlyPayment));
                isValid = false;
            }
        }

        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
