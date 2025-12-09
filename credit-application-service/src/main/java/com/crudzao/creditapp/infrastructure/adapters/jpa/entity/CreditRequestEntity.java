package com.crudzao.creditapp.infrastructure.adapters.jpa.entity;

import com.crudzao.creditapp.domain.enums.CreditRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Credit request entity.
 */
@Entity
@Table(name = "credit_requests", indexes = {
    @Index(name = "idx_affiliate_id", columnList = "affiliate_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Affiliate is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliateEntity affiliate;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Term is required")
    @Positive(message = "Term must be greater than 0")
    @Column(nullable = false)
    private Integer term;

    @NotNull(message = "Rate is required")
    @Positive(message = "Rate must be greater than 0")
    @Column(nullable = false)
    private BigDecimal rate;

    @NotNull(message = "Request date is required")
    @Column(nullable = false)
    private LocalDateTime requestDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditRequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "creditRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private RiskEvaluationEntity riskEvaluation;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
