package com.crudzao.creditapp.application.services;

import com.crudzao.creditapp.domain.enums.RiskLevel;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.AffiliateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for credit approval.
 */
@Slf4j
@Component
public class CreditPolicy {

    private static final BigDecimal MAX_INSTALLMENT_RATIO = BigDecimal.valueOf(0.40);
    private static final BigDecimal MAX_AMOUNT_SALARY_MULTIPLIER = BigDecimal.valueOf(5);
    private static final int MIN_AFFILIATION_MONTHS = 6;

    /**
     * Validates credit request against business rules.
     *
     * @param affiliate customer requesting credit
     * @param amount credit amount
     * @param term term in months
     * @param score risk score from central
     * @param riskLevel risk level classification
     * @return validation result with approval decision
     */
    public ValidationResult validateCreditRequest(
            AffiliateEntity affiliate,
            BigDecimal amount,
            int term,
            int score,
            RiskLevel riskLevel) {

        List<String> rejectionReasons = new ArrayList<>();

        // Rule 1: Minimum affiliation period
        if (!isMinimumAffiliationMet(affiliate.getAffiliationDate())) {
            rejectionReasons.add("Minimum affiliation period not met. Required: " + MIN_AFFILIATION_MONTHS + " months");
        }

        // Rule 2: Maximum installment to salary ratio (40%)
        if (!isInstallmentRatioValid(amount, affiliate.getSalary(), term)) {
            rejectionReasons.add("Monthly installment exceeds 40% of salary");
        }

        // Rule 3: Maximum amount based on salary (5x multiplier)
        if (!isAmountWithinSalaryMultiplier(amount, affiliate.getSalary())) {
            rejectionReasons.add("Credit amount exceeds maximum (5x salary)");
        }

        // Rule 4: Risk level evaluation
        String riskReason = evaluateRiskLevel(score, riskLevel);
        if (riskReason != null) {
            rejectionReasons.add(riskReason);
        }

        boolean approved = rejectionReasons.isEmpty();
        String detail = approved ? "Credit approved based on policies" : String.join("; ", rejectionReasons);

        log.info("Credit validation result: approved={}, detail: {}", approved, detail);

        return new ValidationResult(approved, detail, rejectionReasons);
    }

    /**
     * Checks if minimum affiliation period is met.
     */
    private boolean isMinimumAffiliationMet(LocalDateTime affiliationDate) {
        if (affiliationDate == null) return false;

        LocalDateTime minDate = LocalDateTime.now().minusMonths(MIN_AFFILIATION_MONTHS);
        return affiliationDate.isBefore(minDate);
    }

    /**
     * Checks if monthly installment ratio is within acceptable limit.
     */
    private boolean isInstallmentRatioValid(BigDecimal amount, BigDecimal salary, int term) {
        if (salary.signum() <= 0 || term <= 0) return false;

        BigDecimal monthlyInstallment = amount.divide(BigDecimal.valueOf(term), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal maxInstallment = salary.multiply(MAX_INSTALLMENT_RATIO);

        return monthlyInstallment.compareTo(maxInstallment) <= 0;
    }

    /**
     * Checks if amount is within salary multiplier limit.
     */
    private boolean isAmountWithinSalaryMultiplier(BigDecimal amount, BigDecimal salary) {
        if (salary.signum() <= 0) return false;

        BigDecimal maxAmount = salary.multiply(MAX_AMOUNT_SALARY_MULTIPLIER);
        return amount.compareTo(maxAmount) <= 0;
    }

    /**
     * Evaluates risk level and returns rejection reason if needed.
     */
    private String evaluateRiskLevel(int score, RiskLevel riskLevel) {
        if (riskLevel == RiskLevel.HIGH) {
            return "High risk level. Score: " + score;
        }
        // MEDIUM and LOW risk levels are generally acceptable
        return null;
    }

    /**
     * Result of credit validation.
     */
    @Data
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean approved;
        private String detail;
        private List<String> rejectionReasons;

        public boolean isApproved() {
            return approved;
        }

        public boolean isRejected() {
            return !approved;
        }
    }
}
