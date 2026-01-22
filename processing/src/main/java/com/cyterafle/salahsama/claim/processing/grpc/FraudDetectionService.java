package com.cyterafle.salahsama.claim.processing.grpc;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service de détection de fraude simulé.
 * Dans un vrai système, cela utiliserait du ML ou des règles métier complexes.
 */
@Service
public class FraudDetectionService {

    private final Random random = new Random();

    /**
     * Analyse une réclamation et retourne le niveau de risque
     */
    public FraudAnalysisResult analyzeClaim(
            String claimId,
            String customerId,
            String policyNumber,
            String claimType,
            double claimedAmount,
            String description,
            int claimsLastYear
    ) {
        List<String> riskFactors = new ArrayList<>();
        double riskScore = 0;

        // Règle 1: Montant élevé
        if (claimedAmount > 50000) {
            riskScore += 30;
            riskFactors.add("HIGH_CLAIM_AMOUNT");
        } else if (claimedAmount > 20000) {
            riskScore += 15;
            riskFactors.add("MODERATE_CLAIM_AMOUNT");
        }

        // Règle 2: Nombre de réclamations récentes
        if (claimsLastYear > 3) {
            riskScore += 25;
            riskFactors.add("FREQUENT_CLAIMS");
        } else if (claimsLastYear > 1) {
            riskScore += 10;
            riskFactors.add("MULTIPLE_CLAIMS");
        }

        // Règle 3: Mots-clés suspects dans la description
        String descLower = description != null ? description.toLowerCase() : "";
        if (descLower.contains("total loss") || descLower.contains("perte totale")) {
            riskScore += 20;
            riskFactors.add("TOTAL_LOSS_CLAIM");
        }
        if (descLower.contains("urgent") || descLower.contains("immediately")) {
            riskScore += 10;
            riskFactors.add("URGENCY_INDICATOR");
        }

        // Règle 4: Simulation d'un facteur aléatoire (dans un vrai système, ce serait du ML)
        double randomFactor = random.nextDouble() * 15;
        riskScore += randomFactor;

        // Plafonner le score à 100
        riskScore = Math.min(riskScore, 100);

        // Déterminer le niveau de risque
        RiskLevel riskLevel;
        String recommendation;
        boolean requiresManualReview;

        if (riskScore >= 70) {
            riskLevel = RiskLevel.HIGH;
            recommendation = "REJECT - High fraud probability detected";
            requiresManualReview = true;
        } else if (riskScore >= 40) {
            riskLevel = RiskLevel.MEDIUM;
            recommendation = "REVIEW - Manual verification recommended";
            requiresManualReview = true;
        } else {
            riskLevel = RiskLevel.LOW;
            recommendation = "APPROVE - Low fraud risk";
            requiresManualReview = false;
        }

        return new FraudAnalysisResult(
                claimId,
                riskLevel,
                riskScore,
                riskFactors,
                recommendation,
                requiresManualReview
        );
    }

    /**
     * Obtient le score de risque d'un client (simulé)
     */
    public CustomerRiskResult getCustomerRiskScore(String customerId) {
        // Simulation - dans un vrai système, on interrogerait la DB
        int totalClaims = random.nextInt(10);
        int rejectedClaims = random.nextInt(Math.max(1, totalClaims / 3));
        int approvedClaims = totalClaims - rejectedClaims;
        
        double riskScore = (rejectedClaims * 20.0) + (totalClaims * 5.0);
        riskScore = Math.min(riskScore, 100);
        
        String riskCategory;
        if (riskScore >= 60) {
            riskCategory = "HIGH_RISK";
        } else if (riskScore >= 30) {
            riskCategory = "MEDIUM_RISK";
        } else {
            riskCategory = "LOW_RISK";
        }

        return new CustomerRiskResult(
                customerId,
                riskScore,
                totalClaims,
                rejectedClaims,
                approvedClaims,
                riskCategory
        );
    }

    // Enum pour les niveaux de risque
    public enum RiskLevel {
        UNKNOWN, LOW, MEDIUM, HIGH
    }

    // Classe résultat pour l'analyse de fraude
    public record FraudAnalysisResult(
            String claimId,
            RiskLevel riskLevel,
            double riskScore,
            List<String> riskFactors,
            String recommendation,
            boolean requiresManualReview
    ) {}

    // Classe résultat pour le risque client
    public record CustomerRiskResult(
            String customerId,
            double riskScore,
            int totalClaims,
            int rejectedClaims,
            int approvedClaims,
            String riskCategory
    ) {}
}
