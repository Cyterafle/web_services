package com.cyterafle.salahsama.claim.processing.graphql;

import com.cyterafle.salahsama.claim.processing.entity.*;
import com.cyterafle.salahsama.claim.processing.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ClaimTrackingController {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final CustomerRepository customerRepository;

    public ClaimTrackingController(
            ClaimRepository claimRepository,
            ClaimHistoryRepository claimHistoryRepository,
            CustomerRepository customerRepository
    ) {
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
        this.customerRepository = customerRepository;
    }

    // ============ QUERIES ============

    @QueryMapping
    public Claim claim(@Argument String id) {
        return claimRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @QueryMapping
    public List<Claim> claimsByCustomer(@Argument String customerId) {
        return claimRepository.findByCustomerId(UUID.fromString(customerId));
    }

    @QueryMapping
    public List<Claim> claimsByStatus(@Argument ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    @QueryMapping
    public List<ClaimHistory> claimHistory(@Argument String claimId) {
        return claimHistoryRepository.findByClaimIdOrderByTimestampDesc(UUID.fromString(claimId));
    }

    @QueryMapping
    public ClaimPage allClaims(@Argument Integer page, @Argument Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        
        Page<Claim> claimPage = claimRepository.findAll(PageRequest.of(pageNum, pageSize));
        
        return new ClaimPage(
                claimPage.getContent(),
                claimPage.getTotalElements(),
                claimPage.getTotalPages(),
                claimPage.getNumber()
        );
    }

    // ============ MUTATIONS ============

    @MutationMapping
    public Claim updateClaimStatus(
            @Argument String claimId,
            @Argument ClaimStatus newStatus,
            @Argument String comment
    ) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId))
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        ClaimStatus previousStatus = claim.getStatus();
        claim.setStatus(newStatus);
        claim = claimRepository.save(claim);

        // Enregistrer dans l'historique
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setAction("STATUS_UPDATE");
        history.setDetails(comment);
        history.setPerformedBy("SYSTEM");
        claimHistoryRepository.save(history);

        return claim;
    }

    // ============ SCHEMA MAPPINGS ============

    @SchemaMapping(typeName = "Claim", field = "history")
    public List<ClaimHistory> getClaimHistory(Claim claim) {
        return claimHistoryRepository.findByClaimIdOrderByTimestampDesc(claim.getId());
    }

    // ============ DTOs ============

    public record ClaimPage(
            List<Claim> content,
            long totalElements,
            int totalPages,
            int currentPage
    ) {}
}
