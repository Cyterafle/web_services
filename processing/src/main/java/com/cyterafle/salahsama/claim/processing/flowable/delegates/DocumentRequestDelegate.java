package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service Task: Document Request (REST API simulation)
 * Runs in parallel with Fraud Detection
 */
@Component("documentRequestDelegate")
public class DocumentRequestDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;

    public DocumentRequestDelegate(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String claimType = (String) execution.getVariable("claimType");
        Double claimedAmount = (Double) execution.getVariable("claimedAmount");

        System.out.println("=== DOCUMENT REQUEST (REST) ===");
        System.out.println("Requesting documents for claim: " + claimId);

        // Determine required documents based on claim type and amount
        List<String> requiredDocuments = determineRequiredDocuments(claimType, claimedAmount);

        // For simulation, assume documents are received automatically
        boolean documentsReceived = true;
        boolean documentsValid = true;

        // Update process variables
        execution.setVariable("requiredDocuments", String.join(",", requiredDocuments));
        execution.setVariable("documentsReceived", documentsReceived);
        execution.setVariable("documentsValid", documentsValid);

        // Update claim status
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim != null) {
            claim.setStatus(ClaimStatus.DOCUMENTS_RECEIVED);
            claimRepository.save(claim);
        }

        System.out.println("Required documents: " + requiredDocuments);
        System.out.println("Documents received and valid: " + documentsValid);
    }

    private List<String> determineRequiredDocuments(String claimType, Double amount) {
        if (claimType == null) {
            return Arrays.asList("ID_PROOF", "CLAIM_FORM");
        }

        return switch (claimType.toUpperCase()) {
            case "AUTO" -> amount > 10000 
                    ? Arrays.asList("ID_PROOF", "POLICE_REPORT", "PHOTOS", "REPAIR_ESTIMATE", "VEHICLE_REGISTRATION")
                    : Arrays.asList("ID_PROOF", "PHOTOS", "REPAIR_ESTIMATE");
            case "HOME" -> Arrays.asList("ID_PROOF", "PHOTOS", "PROPERTY_DEED", "DAMAGE_ASSESSMENT");
            case "HEALTH" -> Arrays.asList("ID_PROOF", "MEDICAL_REPORT", "PRESCRIPTIONS", "INVOICES");
            case "LIFE" -> Arrays.asList("ID_PROOF", "DEATH_CERTIFICATE", "BENEFICIARY_ID");
            case "TRAVEL" -> Arrays.asList("ID_PROOF", "TRAVEL_DOCUMENTS", "RECEIPTS", "INCIDENT_REPORT");
            default -> Arrays.asList("ID_PROOF", "CLAIM_FORM");
        };
    }
}
