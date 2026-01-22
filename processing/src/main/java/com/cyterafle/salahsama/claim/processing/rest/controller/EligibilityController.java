package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eligibility")
@Tag(name = "Eligibility & Rules", description = "API pour évaluer l'éligibilité et appliquer les règles métier")
public class EligibilityController {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    // Constantes pour les règles métier
    private static final double HIGH_RISK_AMOUNT_THRESHOLD = 50000.0;
    private static final double MEDIUM_RISK_AMOUNT_THRESHOLD = 20000.0;

    public EligibilityController(
            ClaimRepository claimRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/evaluate/{claimId}")
    @Operation(summary = "Évaluer l'éligibilité d'une réclamation")
    public ResponseEntity<EligibilityResponse> evaluateEligibility(@PathVariable String claimId) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new EligibilityResponse(
                    claimId, false, "CLAIM_NOT_FOUND", List.of("Claim not found"), null
            ));
        }

        List<String> ruleResults = new ArrayList<>();
        boolean eligible = true;
        String decision = "ELIGIBLE";

        // Règle 1: Vérifier si l'identité a été vérifiée
        if (claim.getStatus() == ClaimStatus.IDENTITY_FAILED) {
            eligible = false;
            decision = "REJECTED_IDENTITY_FAILED";
            ruleResults.add("RULE_IDENTITY: FAILED - Identity verification failed");
        } else {
            ruleResults.add("RULE_IDENTITY: PASSED");
        }

        // Règle 2: Vérifier si la police est valide
        if (claim.getStatus() == ClaimStatus.POLICY_INVALID) {
            eligible = false;
            decision = "REJECTED_POLICY_INVALID";
            ruleResults.add("RULE_POLICY: FAILED - Policy is invalid or does not cover claim");
        } else {
            ruleResults.add("RULE_POLICY: PASSED");
        }

        // Règle 3: Vérifier le niveau de fraude
        String fraudLevel = claim.getFraudRiskLevel();
        if ("HIGH".equals(fraudLevel) && claim.getClaimedAmount() > HIGH_RISK_AMOUNT_THRESHOLD) {
            eligible = false;
            decision = "REJECTED_HIGH_FRAUD_RISK";
            ruleResults.add("RULE_FRAUD: FAILED - High fraud risk with high amount");
        } else if ("HIGH".equals(fraudLevel)) {
            ruleResults.add("RULE_FRAUD: WARNING - High fraud risk, requires manual review");
        } else {
            ruleResults.add("RULE_FRAUD: PASSED");
        }

        // Règle 4: Vérifier le montant
        if (claim.getClaimedAmount() > HIGH_RISK_AMOUNT_THRESHOLD) {
            ruleResults.add("RULE_AMOUNT: WARNING - Amount exceeds " + HIGH_RISK_AMOUNT_THRESHOLD + ", requires approval");
        } else {
            ruleResults.add("RULE_AMOUNT: PASSED");
        }

        // Mettre à jour le statut
        ClaimStatus previousStatus = claim.getStatus();
        ClaimStatus newStatus = eligible ? ClaimStatus.ELIGIBLE : ClaimStatus.REJECTED_BY_RULES;
        claim.setStatus(newStatus);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setAction("ELIGIBILITY_EVALUATION");
        history.setDetails(String.join("; ", ruleResults));
        history.setPerformedBy("RULES_ENGINE");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new EligibilityResponse(
                claimId, eligible, decision, ruleResults, newStatus.name()
        ));
    }

    // DTO
    public record EligibilityResponse(
            String claimId,
            boolean eligible,
            String decision,
            List<String> ruleResults,
            String newStatus
    ) {}
}
