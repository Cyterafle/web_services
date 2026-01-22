package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/expert")
@Tag(name = "Expert Assessment", description = "API pour l'évaluation par un expert")
public class ExpertAssessmentController {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    public ExpertAssessmentController(
            ClaimRepository claimRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/assign/{claimId}")
    @Operation(summary = "Assigner une réclamation à un expert")
    public ResponseEntity<ExpertResponse> assignToExpert(
            @PathVariable String claimId,
            @RequestBody ExpertAssignment assignment
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new ExpertResponse(
                    claimId, false, "Claim not found", null, null
            ));
        }

        ClaimStatus previousStatus = claim.getStatus();
        claim.setStatus(ClaimStatus.EXPERT_REVIEW);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(ClaimStatus.EXPERT_REVIEW);
        history.setAction("ASSIGNED_TO_EXPERT");
        history.setDetails("Assigned to expert: " + assignment.expertName());
        history.setPerformedBy("SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new ExpertResponse(
                claimId, true, "Claim assigned to expert", assignment.expertName(), null
        ));
    }

    @PostMapping("/review/{claimId}")
    @Operation(summary = "Soumettre la décision de l'expert")
    public ResponseEntity<ExpertResponse> submitExpertDecision(
            @PathVariable String claimId,
            @RequestBody ExpertDecision decision
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new ExpertResponse(
                    claimId, false, "Claim not found", null, null
            ));
        }

        ClaimStatus previousStatus = claim.getStatus();
        ClaimStatus newStatus = decision.approved() ? ClaimStatus.APPROVED : ClaimStatus.REJECTED;

        claim.setStatus(newStatus);
        claim.setExpertDecision(decision.approved() ? "APPROVED" : "REJECTED");
        claim.setExpertComments(decision.comments());
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setAction("EXPERT_DECISION");
        history.setDetails(decision.comments());
        history.setPerformedBy(decision.expertName());
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new ExpertResponse(
                claimId, true, 
                decision.approved() ? "Claim approved by expert" : "Claim rejected by expert",
                decision.expertName(),
                newStatus.name()
        ));
    }

    // DTOs
    public record ExpertAssignment(String expertName) {}

    public record ExpertDecision(
            boolean approved,
            String comments,
            String expertName
    ) {}

    public record ExpertResponse(
            String claimId,
            boolean success,
            String message,
            String expertName,
            String newStatus
    ) {}
}
