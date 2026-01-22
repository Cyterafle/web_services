package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

/**
 * Service Task: Expert Assessment (REST API simulation)
 */
@Component("expertAssessmentDelegate")
public class ExpertAssessmentDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;
    private final Random random = new Random();

    public ExpertAssessmentDelegate(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String fraudRiskLevel = (String) execution.getVariable("fraudRiskLevel");
        Double claimedAmount = (Double) execution.getVariable("claimedAmount");

        System.out.println("=== EXPERT ASSESSMENT (REST) ===");
        System.out.println("Expert reviewing claim: " + claimId);

        // Simulate expert decision
        // Higher amounts and medium/high fraud risk have lower approval rates
        double approvalProbability = 0.85;
        
        if ("MEDIUM".equals(fraudRiskLevel)) {
            approvalProbability = 0.70;
        } else if ("HIGH".equals(fraudRiskLevel)) {
            approvalProbability = 0.30;
        }
        
        if (claimedAmount > 30000) {
            approvalProbability -= 0.10;
        }

        boolean expertApproved = random.nextDouble() < approvalProbability;
        String expertName = "Expert_" + (random.nextInt(5) + 1);
        String expertComments = expertApproved 
                ? "Claim reviewed and approved. All documentation is in order."
                : "Claim rejected. Insufficient evidence or policy violation detected.";

        // Update process variables
        execution.setVariable("expertApproved", expertApproved);
        execution.setVariable("expertName", expertName);
        execution.setVariable("expertComments", expertComments);

        // Update claim
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim != null) {
            claim.setStatus(expertApproved ? ClaimStatus.APPROVED : ClaimStatus.REJECTED);
            claim.setExpertDecision(expertApproved ? "APPROVED" : "REJECTED");
            claim.setExpertComments(expertComments);
            claimRepository.save(claim);
        }

        System.out.println("Expert: " + expertName);
        System.out.println("Decision: " + (expertApproved ? "APPROVED" : "REJECTED"));
    }
}
