package com.cyterafle.salahsama.claim.processing.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByCustomerId(UUID customerId);
    List<Claim> findByStatus(ClaimStatus status);
    List<Claim> findByPolicyNumber(String policyNumber);
}
