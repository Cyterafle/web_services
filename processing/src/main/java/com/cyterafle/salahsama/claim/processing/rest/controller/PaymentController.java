package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment Authorization", description = "API pour autoriser les paiements")
public class PaymentController {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final Random random = new Random();

    public PaymentController(
            ClaimRepository claimRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/authorize/{claimId}")
    @Operation(summary = "Autoriser le paiement d'une réclamation")
    public ResponseEntity<PaymentResponse> authorizePayment(@PathVariable String claimId) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new PaymentResponse(
                    claimId, false, "Claim not found", null, 0, null
            ));
        }

        // Vérifier que la compensation a été calculée
        if (claim.getStatus() != ClaimStatus.COMPENSATION_CALCULATED) {
            return ResponseEntity.badRequest().body(new PaymentResponse(
                    claimId, false, 
                    "Compensation must be calculated first. Current status: " + claim.getStatus(),
                    null, 0, null
            ));
        }

        // Simulation: 95% de chances de succès
        boolean authorized = random.nextDouble() < 0.95;
        String transactionId = authorized ? "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : null;

        ClaimStatus previousStatus = claim.getStatus();
        ClaimStatus newStatus = authorized ? ClaimStatus.PAYMENT_AUTHORIZED : ClaimStatus.PAYMENT_FAILED;

        claim.setStatus(newStatus);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setAction("PAYMENT_AUTHORIZATION");
        history.setDetails(authorized 
                ? "Payment authorized. Transaction ID: " + transactionId 
                : "Payment authorization failed");
        history.setPerformedBy("PAYMENT_SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new PaymentResponse(
                claimId, authorized,
                authorized ? "Payment authorized successfully" : "Payment authorization failed",
                transactionId,
                claim.getApprovedAmount() != null ? claim.getApprovedAmount() : 0,
                newStatus.name()
        ));
    }

    @PostMapping("/complete/{claimId}")
    @Operation(summary = "Marquer le paiement comme complété")
    public ResponseEntity<PaymentResponse> completePayment(@PathVariable String claimId) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new PaymentResponse(
                    claimId, false, "Claim not found", null, 0, null
            ));
        }

        if (claim.getStatus() != ClaimStatus.PAYMENT_AUTHORIZED) {
            return ResponseEntity.badRequest().body(new PaymentResponse(
                    claimId, false, 
                    "Payment must be authorized first. Current status: " + claim.getStatus(),
                    null, 0, null
            ));
        }

        ClaimStatus previousStatus = claim.getStatus();
        claim.setStatus(ClaimStatus.COMPLETED);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(ClaimStatus.COMPLETED);
        history.setAction("PAYMENT_COMPLETED");
        history.setDetails("Payment completed and claim closed");
        history.setPerformedBy("PAYMENT_SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new PaymentResponse(
                claimId, true, "Payment completed. Claim is now closed.",
                null, claim.getApprovedAmount() != null ? claim.getApprovedAmount() : 0,
                ClaimStatus.COMPLETED.name()
        ));
    }

    // DTO
    public record PaymentResponse(
            String claimId,
            boolean success,
            String message,
            String transactionId,
            double amount,
            String newStatus
    ) {}
}
