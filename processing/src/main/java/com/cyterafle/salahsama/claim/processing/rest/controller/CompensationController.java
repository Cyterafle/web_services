package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/compensation")
@Tag(name = "Compensation Calculation", description = "API pour calculer la compensation")
public class CompensationController {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    // Taux de déduction selon le type
    private static final double DEDUCTIBLE_RATE_AUTO = 0.10;
    private static final double DEDUCTIBLE_RATE_HOME = 0.05;
    private static final double DEDUCTIBLE_RATE_HEALTH = 0.20;
    private static final double DEDUCTIBLE_RATE_LIFE = 0.0;
    private static final double DEDUCTIBLE_RATE_TRAVEL = 0.15;

    public CompensationController(
            ClaimRepository claimRepository,
            PolicyRepository policyRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/calculate/{claimId}")
    @Operation(summary = "Calculer la compensation pour une réclamation approuvée")
    public ResponseEntity<CompensationResponse> calculateCompensation(@PathVariable String claimId) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new CompensationResponse(
                    claimId, false, "Claim not found", 0, 0, 0, null
            ));
        }

        // Vérifier que la réclamation est approuvée
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            return ResponseEntity.badRequest().body(new CompensationResponse(
                    claimId, false, "Claim is not approved. Current status: " + claim.getStatus(),
                    0, 0, 0, null
            ));
        }

        double claimedAmount = claim.getClaimedAmount();
        double deductibleRate = getDeductibleRate(claim.getClaimType());
        double deductibleAmount = claimedAmount * deductibleRate;
        double approvedAmount = claimedAmount - deductibleAmount;

        // Vérifier la couverture de la police si disponible
        if (claim.getPolicy() != null && claim.getPolicy().getCoverageAmount() != null) {
            approvedAmount = Math.min(approvedAmount, claim.getPolicy().getCoverageAmount());
        }

        // Mettre à jour la réclamation
        ClaimStatus previousStatus = claim.getStatus();
        claim.setApprovedAmount(approvedAmount);
        claim.setStatus(ClaimStatus.COMPENSATION_CALCULATED);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(ClaimStatus.COMPENSATION_CALCULATED);
        history.setAction("COMPENSATION_CALCULATED");
        history.setDetails(String.format("Claimed: %.2f, Deductible: %.2f (%.0f%%), Approved: %.2f",
                claimedAmount, deductibleAmount, deductibleRate * 100, approvedAmount));
        history.setPerformedBy("SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new CompensationResponse(
                claimId, true, "Compensation calculated successfully",
                claimedAmount, deductibleAmount, approvedAmount,
                ClaimStatus.COMPENSATION_CALCULATED.name()
        ));
    }

    private double getDeductibleRate(String claimType) {
        if (claimType == null) return 0.10;
        
        return switch (claimType.toUpperCase()) {
            case "AUTO" -> DEDUCTIBLE_RATE_AUTO;
            case "HOME" -> DEDUCTIBLE_RATE_HOME;
            case "HEALTH" -> DEDUCTIBLE_RATE_HEALTH;
            case "LIFE" -> DEDUCTIBLE_RATE_LIFE;
            case "TRAVEL" -> DEDUCTIBLE_RATE_TRAVEL;
            default -> 0.10;
        };
    }

    // DTO
    public record CompensationResponse(
            String claimId,
            boolean success,
            String message,
            double claimedAmount,
            double deductibleAmount,
            double approvedAmount,
            String newStatus
    ) {}
}
