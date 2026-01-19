package com.cyterafle.salahsama.claim.processing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cyterafle.salahsama.claim.processing.entity.Claim;

@EnableJpaRepositories
public interface ClaimRepository extends JpaRepository<Claim, UUID>{
    
}
