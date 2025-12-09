package com.crudzao.creditapp.infrastructure.adapters.jpa.repository;

import com.crudzao.creditapp.domain.enums.CreditRequestStatus;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.CreditRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CreditRequest entity.
 */
@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequestEntity, Long> {
    List<CreditRequestEntity> findByAffiliateId(Long affiliateId);
    List<CreditRequestEntity> findByStatus(CreditRequestStatus status);
}
