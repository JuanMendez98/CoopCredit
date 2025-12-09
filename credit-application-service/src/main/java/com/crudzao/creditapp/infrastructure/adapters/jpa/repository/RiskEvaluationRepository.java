package com.crudzao.creditapp.infrastructure.adapters.jpa.repository;

import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.RiskEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for RiskEvaluation entity.
 */
@Repository
public interface RiskEvaluationRepository extends JpaRepository<RiskEvaluationEntity, Long> {
}
