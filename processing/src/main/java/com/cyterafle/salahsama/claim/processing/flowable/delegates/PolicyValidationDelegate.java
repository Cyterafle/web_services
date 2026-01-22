package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.entity.Policy;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import com.cyterafle.salahsama.claim.processing.repository.PolicyRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Task: Policy Validation (SOAP API simulation)
 * This simulates calling the SOAP web service internally
 */
@Component("policyValidationDelegate")
public class PolicyValidationDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public PolicyValidationDelegate(ClaimRepository claimRepository, PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String policyNumber = (String) execution.getVariable("policyNumber");
        String customerId = (String) execution.getVariable("customerId");
        String claimType = (String) execution.getVariable("claimType");
        Double claimedAmount = (Double) execution.getVariable("claimedAmount");

        System.out.println("=== POLICY VALIDATION (SOAP) ===");
        System.out.println("Policy Number: " + policyNumber);

        boolean policyValid = false;
        String validationMessage = "";

        // Find policy
        Optional<Policy> policyOpt = policyRepository.findByPolicyNumber(policyNumber);

        if (policyOpt.isEmpty()) {
            validationMessage = "Policy not found";
        } else {
            Policy policy = policyOpt.get();

            // Check customer ownership
            if (!policy.getCustomer().getId().toString().equals(customerId)) {
                validationMessage = "Policy does not belong to customer";
            }
            // Check policy validity
            else if (!policy.isValid()) {
                validationMessage = "Policy is inactive or expired";
            }
            // Check claim type coverage
            else if (!policy.getType().name().equalsIgnoreCase(claimType)) {
                validationMessage = "Policy type does not cover claim type";
            }
            // Check coverage amount
            else if (claimedAmount > policy.getCoverageAmount()) {
                validationMessage = "Claimed amount exceeds coverage";
            }
            // All checks passed
            else {
                policyValid = true;
                validationMessage = "Policy is valid";
                
                // Link policy to claim
                Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
                if (claim != null) {
                    claim.setPolicy(policy);
                    claimRepository.save(claim);
                }
            }
        }

        // Update process variables
        execution.setVariable("policyValid", policyValid);
        execution.setVariable("policyValidationMessage", validationMessage);

        // Update claim status
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim != null) {
            claim.setStatus(policyValid ? ClaimStatus.POLICY_VALIDATED : ClaimStatus.POLICY_INVALID);
            claimRepository.save(claim);
        }

        System.out.println("Policy valid: " + policyValid + " - " + validationMessage);
    }
}
