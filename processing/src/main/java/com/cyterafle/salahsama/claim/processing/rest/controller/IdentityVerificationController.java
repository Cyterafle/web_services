package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/identity")
@Tag(name = "Identity Verification", description = "API pour vérifier l'identité des clients")
public class IdentityVerificationController {

    private final CustomerRepository customerRepository;
    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    public IdentityVerificationController(
            CustomerRepository customerRepository,
            ClaimRepository claimRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.customerRepository = customerRepository;
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping("/verify")
    @Operation(summary = "Vérifier l'identité d'un client")
    public ResponseEntity<IdentityVerificationResponse> verifyIdentity(
            @RequestBody IdentityVerificationRequest request
    ) {
        // Chercher le client par email
        Customer customer = customerRepository.findByMail(request.email()).orElse(null);

        if (customer == null) {
            return ResponseEntity.ok(new IdentityVerificationResponse(
                    false, null, "Customer not found with email: " + request.email()
            ));
        }

        // Vérifier nom et prénom
        boolean nameMatch = customer.getName().equalsIgnoreCase(request.name());
        boolean surnameMatch = customer.getSurname().equalsIgnoreCase(request.surname());

        if (!nameMatch || !surnameMatch) {
            return ResponseEntity.ok(new IdentityVerificationResponse(
                    false, customer.getId().toString(), "Name or surname does not match"
            ));
        }

        // Marquer le client comme vérifié
        customer.setVerified(true);
        customerRepository.save(customer);

        return ResponseEntity.ok(new IdentityVerificationResponse(
                true, customer.getId().toString(), "Identity verified successfully"
        ));
    }

    @PostMapping("/verify-for-claim/{claimId}")
    @Operation(summary = "Vérifier l'identité pour une réclamation spécifique")
    public ResponseEntity<IdentityVerificationResponse> verifyIdentityForClaim(
            @PathVariable String claimId,
            @RequestBody IdentityVerificationRequest request
    ) {
        // Récupérer la réclamation
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);

        if (claim == null) {
            return ResponseEntity.badRequest().body(new IdentityVerificationResponse(
                    false, null, "Claim not found"
            ));
        }

        // Vérifier l'identité
        ResponseEntity<IdentityVerificationResponse> verificationResult = verifyIdentity(request);
        IdentityVerificationResponse response = verificationResult.getBody();

        // Mettre à jour le statut de la réclamation
        ClaimStatus previousStatus = claim.getStatus();
        ClaimStatus newStatus = response.verified() 
                ? ClaimStatus.IDENTITY_VERIFIED 
                : ClaimStatus.IDENTITY_FAILED;

        claim.setStatus(newStatus);
        claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setAction("IDENTITY_VERIFICATION");
        history.setDetails(response.message());
        history.setPerformedBy("SYSTEM");
        claimHistoryRepository.save(history);

        return ResponseEntity.ok(response);
    }

    // DTOs
    public record IdentityVerificationRequest(
            String name,
            String surname,
            String email
    ) {}

    public record IdentityVerificationResponse(
            boolean verified,
            String customerId,
            String message
    ) {}
}
