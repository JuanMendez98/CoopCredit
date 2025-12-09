package com.crudzao.creditapp.infrastructure.adapters.jpa.entity;

import com.crudzao.creditapp.domain.enums.CreditDecision;
import com.crudzao.creditapp.domain.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Risk evaluation entity.
 */
@Entity
@Table(name = "risk_evaluations", indexes = {
    @Index(name = "idx_credit_request_id", columnList = "credit_request_id"),
    @Index(name = "idx_decision", columnList = "decision")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Credit request is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_request_id", nullable = false, unique = true)
    private CreditRequestEntity creditRequest;

    @NotNull(message = "Score is required")
    @Column(nullable = false)
    private Integer score;

    @NotNull(message = "Risk level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @NotNull(message = "Decision is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditDecision decision;

    @Column(length = 500)
    private String reason;

    @NotNull(message = "Evaluation date is required")
    @Column(nullable = false)
    private LocalDateTime evaluationDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
