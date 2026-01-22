package com.cyterafle.salahsama.claim.processing.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cyterafle.salahsama.claim.processing.entity.ClaimHistory;

@Repository
public interface ClaimHistoryRepository extends JpaRepository<ClaimHistory, UUID> {
    List<ClaimHistory> findByClaimIdOrderByTimestampDesc(UUID claimId);
}
