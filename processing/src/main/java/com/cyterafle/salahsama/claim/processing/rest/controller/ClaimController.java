package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/claims")
@Tag(name = "Claim Submission", description = "API pour soumettre et gérer les réclamations")
public class ClaimController {

    private final ClaimRepository claimRepository;
    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    public ClaimController(
            ClaimRepository claimRepository,
            CustomerRepository customerRepository,
            PolicyRepository policyRepository,
            ClaimHistoryRepository claimHistoryRepository
    ) {
        this.claimRepository = claimRepository;
        this.customerRepository = customerRepository;
        this.policyRepository = policyRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @PostMapping
    @Operation(summary = "Soumettre une nouvelle réclamation")
    public ResponseEntity<ClaimResponse> submitClaim(@RequestBody ClaimSubmissionRequest request) {
        // Vérifier que le client existe
        Customer customer = customerRepository.findById(UUID.fromString(request.customerId()))
                .orElse(null);
        
        if (customer == null) {
            return ResponseEntity.badRequest()
                    .body(new ClaimResponse(null, false, "Customer not found"));
        }

        // Créer la réclamation
        Claim claim = new Claim();
        claim.setCustomer(customer);
        claim.setPolicyNumber(request.policyNumber());
        claim.setClaimType(request.claimType());
        claim.setClaimedAmount(request.claimedAmount());
        claim.setDescription(request.description());
        claim.setStatus(ClaimStatus.SUBMITTED);

        claim = claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setNewStatus(ClaimStatus.SUBMITTED);
        history.setAction("CLAIM_SUBMITTED");
        history.setDetails("Claim submitted by customer");
        history.setPerformedBy("CUSTOMER");
        claimHistoryRepository.save(history);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ClaimResponse(claim.getId().toString(), true, "Claim submitted successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réclamation par ID")
    public ResponseEntity<Claim> getClaim(@PathVariable String id) {
        return claimRepository.findById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les réclamations")
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Récupérer les réclamations d'un client")
    public List<Claim> getClaimsByCustomer(@PathVariable String customerId) {
        return claimRepository.findByCustomerId(UUID.fromString(customerId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Récupérer les réclamations par statut")
    public List<Claim> getClaimsByStatus(@PathVariable ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    // DTOs
    public record ClaimSubmissionRequest(
            String customerId,
            String policyNumber,
            String claimType,
            Double claimedAmount,
            String description
    ) {}

    public record ClaimResponse(
            String claimId,
            boolean success,
            String message
    ) {}
}
