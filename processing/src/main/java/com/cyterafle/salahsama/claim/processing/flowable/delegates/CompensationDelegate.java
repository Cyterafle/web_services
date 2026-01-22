package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service Task: Compensation Calculation (REST API simulation)
 */
@Component("compensationDelegate")
public class CompensationDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;

    public CompensationDelegate(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String claimType = (String) execution.getVariable("claimType");
        Double claimedAmount = (Double) execution.getVariable("claimedAmount");

        System.out.println("=== COMPENSATION CALCULATION (REST) ===");
        System.out.println("Calculating compensation for claim: " + claimId);

        // Calculate deductible based on claim type
        double deductibleRate = getDeductibleRate(claimType);
        double deductibleAmount = claimedAmount * deductibleRate;
        double approvedAmount = claimedAmount - deductibleAmount;

        // Update process variables
        execution.setVariable("deductibleRate", deductibleRate);
        execution.setVariable("deductibleAmount", deductibleAmount);
        execution.setVariable("approvedAmount", approvedAmount);

        // Update claim
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim != null) {
            claim.setApprovedAmount(approvedAmount);
            claim.setStatus(ClaimStatus.COMPENSATION_CALCULATED);
            claimRepository.save(claim);
        }

        System.out.println("Claimed: " + claimedAmount);
        System.out.println("Deductible (" + (deductibleRate * 100) + "%): " + deductibleAmount);
        System.out.println("Approved amount: " + approvedAmount);
    }

    private double getDeductibleRate(String claimType) {
        if (claimType == null) return 0.10;
        
        return switch (claimType.toUpperCase()) {
            case "AUTO" -> 0.10;
            case "HOME" -> 0.05;
            case "HEALTH" -> 0.20;
            case "LIFE" -> 0.0;
            case "TRAVEL" -> 0.15;
            default -> 0.10;
        };
    }
}
