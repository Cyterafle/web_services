package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service Task: Eligibility Evaluation (REST API simulation)
 */
@Component("eligibilityDelegate")
public class EligibilityDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;
    private static final double HIGH_RISK_THRESHOLD = 50000.0;

    public EligibilityDelegate(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String fraudRiskLevel = (String) execution.getVariable("fraudRiskLevel");
        Double claimedAmount = (Double) execution.getVariable("claimedAmount");
        Boolean documentsValid = (Boolean) execution.getVariable("documentsValid");

        System.out.println("=== ELIGIBILITY EVALUATION (REST) ===");
        System.out.println("Evaluating claim: " + claimId);

        List<String> ruleResults = new ArrayList<>();
        boolean eligible = true;

        // Rule 1: Documents must be valid
        if (documentsValid == null || !documentsValid) {
            eligible = false;
            ruleResults.add("DOCUMENTS: FAILED");
        } else {
            ruleResults.add("DOCUMENTS: PASSED");
        }

        // Rule 2: High fraud risk with high amount = automatic rejection
        if ("HIGH".equals(fraudRiskLevel) && claimedAmount > HIGH_RISK_THRESHOLD) {
            eligible = false;
            ruleResults.add("FRAUD_AMOUNT_RULE: FAILED - High risk with high amount");
        } else {
            ruleResults.add("FRAUD_AMOUNT_RULE: PASSED");
        }

        // Rule 3: Medium fraud risk requires manual review but is eligible
        if ("MEDIUM".equals(fraudRiskLevel)) {
            ruleResults.add("FRAUD_LEVEL: WARNING - Requires manual review");
        } else {
            ruleResults.add("FRAUD_LEVEL: OK");
        }

        // Update process variables
        execution.setVariable("eligible", eligible);
        execution.setVariable("eligibilityRules", String.join("; ", ruleResults));

        // Update claim status
        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim != null) {
            claim.setStatus(eligible ? ClaimStatus.ELIGIBLE : ClaimStatus.REJECTED_BY_RULES);
            claimRepository.save(claim);
        }

        System.out.println("Eligible: " + eligible);
        System.out.println("Rules: " + ruleResults);
    }
}
