package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.grpc.FraudDetectionService;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service Task: Fraud Detection (gRPC API simulation)
 * Calls the internal FraudDetectionService which simulates gRPC
 */
@Component("fraudDetectionDelegate")
public class FraudDetectionDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;
    private final FraudDetectionService fraudDetectionService;

    public FraudDetectionDelegate(ClaimRepository claimRepository, FraudDetectionService fraudDetectionService) {
        this.claimRepository = claimRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String customerId = (String) execution.getVariable("customerId");
        String policyNumber = (String) execution.getVariable("policyNumber");
        String claimType = (String) execution.getVariable("claimType");
        Double claimedAmount = (Double) execution.getVariable("claimedAmount");
        String description = (String) execution.getVariable("description");

        System.out.println("=== FRAUD DETECTION (gRPC) ===");
        System.out.println("Analyzing claim: " + claimId);

        // Call fraud detection service
        FraudDetectionService.FraudAnalysisResult result = fraudDetectionService.analyzeClaim(
                claimId,
                customerId,
                policyNumber,
                claimType,
                claimedAmount,
                description,
                1  // Simulated claims last year
        );

        String riskLevel = result.riskLevel().name();
        double riskScore = result.riskScore();

        // Update process variables
        execution.setVariable("fraudRiskLevel", riskLevel);
        execution.setVariable("fraudRiskScore", riskScore);
        execution.setVariable("fraudRecommendation", result.recommendation());
        execution.setVariable("requiresManualReview", result.requiresManualReview());

        // Update claim
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim != null) {
            claim.setFraudRiskLevel(riskLevel);
            
            // Set appropriate status based on risk level
            ClaimStatus newStatus = switch (riskLevel) {
                case "LOW" -> ClaimStatus.FRAUD_CHECK_LOW;
                case "MEDIUM" -> ClaimStatus.FRAUD_CHECK_MEDIUM;
                case "HIGH" -> ClaimStatus.FRAUD_CHECK_HIGH;
                default -> ClaimStatus.FRAUD_CHECK_MEDIUM;
            };
            claim.setStatus(newStatus);
            claimRepository.save(claim);
        }

        System.out.println("Fraud risk level: " + riskLevel + " (Score: " + riskScore + ")");
    }
}
