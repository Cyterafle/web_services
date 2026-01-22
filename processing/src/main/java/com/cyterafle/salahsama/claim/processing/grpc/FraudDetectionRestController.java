package com.cyterafle.salahsama.claim.processing.grpc;

import org.springframework.web.bind.annotation.*;

/**
 * Controller REST qui expose les mêmes fonctionnalités que le service gRPC.
 * Utile pour les tests et comme fallback.
 */
@RestController
@RequestMapping("/api/fraud")
public class FraudDetectionRestController {

    private final FraudDetectionService fraudDetectionService;

    public FraudDetectionRestController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @PostMapping("/analyze")
    public FraudDetectionService.FraudAnalysisResult analyzeClaim(@RequestBody FraudAnalysisRequest request) {
        return fraudDetectionService.analyzeClaim(
                request.claimId(),
                request.customerId(),
                request.policyNumber(),
                request.claimType(),
                request.claimedAmount(),
                request.description(),
                request.claimsLastYear()
        );
    }

    @GetMapping("/customer-risk/{customerId}")
    public FraudDetectionService.CustomerRiskResult getCustomerRisk(@PathVariable String customerId) {
        return fraudDetectionService.getCustomerRiskScore(customerId);
    }

    // DTO pour la requête d'analyse
    public record FraudAnalysisRequest(
            String claimId,
            String customerId,
            String policyNumber,
            String claimType,
            double claimedAmount,
            String description,
            int claimsLastYear
    ) {}
}
