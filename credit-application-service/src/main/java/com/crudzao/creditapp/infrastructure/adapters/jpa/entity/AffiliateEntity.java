package com.crudzao.creditapp.infrastructure.adapters.jpa.entity;

import com.crudzao.creditapp.domain.enums.AffiliateStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Affiliate entity representing a member of the credit union.
 */
@Entity
@Table(name = "affiliates", indexes = {
    @Index(name = "idx_document", columnList = "document")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Document is required")
    @Column(nullable = false, unique = true, length = 20)
    private String document;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be greater than 0")
    @Column(nullable = false)
    private BigDecimal salary;

    @NotNull(message = "Affiliation date is required")
    @Column(nullable = false)
    private LocalDateTime affiliationDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AffiliateStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "affiliate", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CreditRequestEntity> creditRequests;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
