package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Review", description = "API pour la gestion et revue des documents")
public class DocumentReviewController {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    public DocumentReviewController(
            ClaimRepository claimRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/request/{claimId}")
    @Operation(summary = "Demander des documents supplémentaires")
    public ResponseEntity<DocumentResponse> requestDocuments(
            @PathVariable String claimId,
            @RequestBody DocumentRequest request
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new DocumentResponse(
                    claimId, false, "Claim not found", null
            ));
        }

        ClaimStatus previousStatus = claim.getStatus();
        claim.setStatus(ClaimStatus.DOCUMENTS_REQUESTED);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(ClaimStatus.DOCUMENTS_REQUESTED);
        history.setAction("DOCUMENTS_REQUESTED");
        history.setDetails("Requested documents: " + String.join(", ", request.requiredDocuments()));
        history.setPerformedBy("SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new DocumentResponse(
                claimId, true, "Documents requested successfully", request.requiredDocuments()
        ));
    }

    @PostMapping("/submit/{claimId}")
    @Operation(summary = "Soumettre des documents (simulation)")
    public ResponseEntity<DocumentResponse> submitDocuments(
            @PathVariable String claimId,
            @RequestBody DocumentSubmission submission
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new DocumentResponse(
                    claimId, false, "Claim not found", null
            ));
        }

        ClaimStatus previousStatus = claim.getStatus();
        claim.setStatus(ClaimStatus.DOCUMENTS_RECEIVED);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(ClaimStatus.DOCUMENTS_RECEIVED);
        history.setAction("DOCUMENTS_SUBMITTED");
        history.setDetails("Submitted documents: " + String.join(", ", submission.documentNames()));
        history.setPerformedBy("CUSTOMER");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new DocumentResponse(
                claimId, true, "Documents submitted successfully", submission.documentNames()
        ));
    }

    @PostMapping("/validate/{claimId}")
    @Operation(summary = "Valider les documents soumis")
    public ResponseEntity<DocumentValidationResponse> validateDocuments(
            @PathVariable String claimId,
            @RequestBody DocumentValidation validation
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new DocumentValidationResponse(
                    claimId, false, "Claim not found", null
            ));
        }

        ClaimStatus previousStatus = claim.getStatus();
        ClaimStatus newStatus = validation.valid() 
                ? ClaimStatus.DOCUMENTS_RECEIVED 
                : ClaimStatus.DOCUMENTS_INVALID;

        claim.setStatus(newStatus);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setAction("DOCUMENTS_VALIDATION");
        history.setDetails(validation.comments());
        history.setPerformedBy(validation.reviewedBy());
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new DocumentValidationResponse(
                claimId, validation.valid(), 
                validation.valid() ? "Documents validated" : "Documents invalid",
                newStatus.name()
        ));
    }

    // DTOs
    public record DocumentRequest(List<String> requiredDocuments) {}

    public record DocumentSubmission(List<String> documentNames) {}

    public record DocumentValidation(boolean valid, String comments, String reviewedBy) {}

    public record DocumentResponse(
            String claimId,
            boolean success,
            String message,
            List<String> documents
    ) {}

    public record DocumentValidationResponse(
            String claimId,
            boolean valid,
            String message,
            String newStatus
    ) {}
}
