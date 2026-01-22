package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.entity.Customer;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service Task: Identity Verification (REST API simulation)
 */
@Component("identityVerificationDelegate")
public class IdentityVerificationDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;

    public IdentityVerificationDelegate(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        
        System.out.println("=== IDENTITY VERIFICATION (REST) ===");
        System.out.println("Processing claim: " + claimId);

        Claim claim = claimRepository.findById(UUID.fromString(claimId))
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        Customer customer = claim.getCustomer();
        
        // Simulate identity verification logic
        boolean identityVerified = customer != null && 
                                   customer.getVerified() != null && 
                                   customer.getVerified();

        // If not pre-verified, check if customer data is complete
        if (!identityVerified && customer != null) {
            identityVerified = customer.getName() != null && 
                              customer.getSurname() != null && 
                              customer.getMail() != null;
        }

        // Update process variable
        execution.setVariable("identityVerified", identityVerified);

        // Update claim status
        claim.setStatus(identityVerified ? ClaimStatus.IDENTITY_VERIFIED : ClaimStatus.IDENTITY_FAILED);
        claimRepository.save(claim);

        System.out.println("Identity verified: " + identityVerified);
    }
}
