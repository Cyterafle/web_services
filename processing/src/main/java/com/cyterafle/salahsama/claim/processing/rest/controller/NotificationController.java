package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Customer Notification", description = "API pour notifier les clients")
public class NotificationController {

    private final ClaimRepository claimRepository;
    private final CustomerRepository customerRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    public NotificationController(
            ClaimRepository claimRepository,
            CustomerRepository customerRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.customerRepository = customerRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/send/{claimId}")
    @Operation(summary = "Envoyer une notification au client concernant sa réclamation")
    public ResponseEntity<NotificationResponse> sendNotification(
            @PathVariable String claimId,
            @RequestBody NotificationRequest request
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new NotificationResponse(
                    false, "Claim not found", null, null, null
            ));
        }

        Customer customer = claim.getCustomer();
        if (customer == null) {
            return ResponseEntity.badRequest().body(new NotificationResponse(
                    false, "Customer not found for this claim", null, null, null
            ));
        }

        // Générer le message basé sur le statut si non fourni
        String message = request.message();
        if (message == null || message.isEmpty()) {
            message = generateStatusMessage(claim);
        }

        // Simulation d'envoi de notification (email)
        String notificationId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(claim.getStatus());
        history.setNewStatus(claim.getStatus());
        history.setAction("NOTIFICATION_SENT");
        history.setDetails("Notification sent to " + customer.getMail() + ": " + message);
        history.setPerformedBy("NOTIFICATION_SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(new NotificationResponse(
                true, "Notification sent successfully",
                notificationId,
                customer.getMail(),
                message
        ));
    }

    @PostMapping("/send-status-update/{claimId}")
    @Operation(summary = "Envoyer une notification de mise à jour de statut")
    public ResponseEntity<NotificationResponse> sendStatusUpdateNotification(@PathVariable String claimId) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new NotificationResponse(
                    false, "Claim not found", null, null, null
            ));
        }

        String message = generateStatusMessage(claim);
        return sendNotification(claimId, new NotificationRequest(message, "STATUS_UPDATE"));
    }

    @GetMapping("/history/{claimId}")
    @Operation(summary = "Récupérer l'historique des notifications d'une réclamation")
    public ResponseEntity<List<ClaimHistory>> getNotificationHistory(@PathVariable String claimId) {
        List<ClaimHistory> history = claimHistoryRepository.findByClaimIdOrderByTimestampDesc(
                UUID.fromString(claimId)
        );

        List<ClaimHistory> notifications = history.stream()
                .filter(h -> "NOTIFICATION_SENT".equals(h.getAction()))
                .toList();

        return ResponseEntity.ok(notifications);
    }

    private String generateStatusMessage(Claim claim) {
        String claimRef = claim.getId().toString().substring(0, 8).toUpperCase();
        
        return switch (claim.getStatus()) {
            case SUBMITTED -> String.format(
                    "Your claim #%s has been submitted successfully. We will process it shortly.", claimRef);
            case IDENTITY_VERIFIED -> String.format(
                    "Your identity for claim #%s has been verified.", claimRef);
            case IDENTITY_FAILED -> String.format(
                    "Identity verification failed for claim #%s. Please contact support.", claimRef);
            case POLICY_VALIDATED -> String.format(
                    "Your policy for claim #%s has been validated.", claimRef);
            case POLICY_INVALID -> String.format(
                    "Your policy does not cover claim #%s. The claim has been rejected.", claimRef);
            case FRAUD_CHECK_LOW, FRAUD_CHECK_MEDIUM -> String.format(
                    "Claim #%s is being reviewed.", claimRef);
            case FRAUD_CHECK_HIGH -> String.format(
                    "Claim #%s requires additional review due to risk assessment.", claimRef);
            case ELIGIBLE -> String.format(
                    "Claim #%s is eligible for processing.", claimRef);
            case REJECTED_BY_RULES -> String.format(
                    "Claim #%s has been rejected based on policy rules.", claimRef);
            case DOCUMENTS_REQUESTED -> String.format(
                    "Additional documents are required for claim #%s.", claimRef);
            case DOCUMENTS_RECEIVED -> String.format(
                    "Documents for claim #%s have been received and are being reviewed.", claimRef);
            case DOCUMENTS_INVALID -> String.format(
                    "Documents for claim #%s are invalid. Please resubmit.", claimRef);
            case EXPERT_REVIEW -> String.format(
                    "Claim #%s is under expert review.", claimRef);
            case APPROVED -> String.format(
                    "Great news! Claim #%s has been approved.", claimRef);
            case REJECTED -> String.format(
                    "We regret to inform you that claim #%s has been rejected.", claimRef);
            case COMPENSATION_CALCULATED -> String.format(
                    "Compensation for claim #%s has been calculated: %.2f EUR.", 
                    claimRef, claim.getApprovedAmount());
            case PAYMENT_AUTHORIZED -> String.format(
                    "Payment for claim #%s has been authorized.", claimRef);
            case PAYMENT_FAILED -> String.format(
                    "Payment for claim #%s failed. We will retry shortly.", claimRef);
            case COMPLETED -> String.format(
                    "Claim #%s is now complete. Payment of %.2f EUR has been processed.", 
                    claimRef, claim.getApprovedAmount());
            case SUSPENDED -> String.format(
                    "Claim #%s has been suspended. Please contact support.", claimRef);
        };
    }

    // DTOs
    public record NotificationRequest(String message, String type) {}

    public record NotificationResponse(
            boolean success,
            String message,
            String notificationId,
            String recipientEmail,
            String notificationContent
    ) {}
}
