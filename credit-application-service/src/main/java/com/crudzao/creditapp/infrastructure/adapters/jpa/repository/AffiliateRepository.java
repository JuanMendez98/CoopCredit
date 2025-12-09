package com.crudzao.creditapp.infrastructure.adapters.jpa.repository;

import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Affiliate entity.
 */
@Repository
public interface AffiliateRepository extends JpaRepository<AffiliateEntity, Long> {
    Optional<AffiliateEntity> findByDocument(String document);
}
