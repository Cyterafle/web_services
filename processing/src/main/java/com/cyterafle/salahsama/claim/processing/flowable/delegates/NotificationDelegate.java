package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimHistory;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.repository.ClaimHistoryRepository;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service Task: Customer Notification (REST API simulation)
 * Sends notification to customer about claim status
 */
@Component("notificationDelegate")
public class NotificationDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    public NotificationDelegate(ClaimRepository claimRepository, ClaimHistoryRepository claimHistoryRepository) {
        this.claimRepository = claimRepository;
        this.claimHistoryRepository = claimHistoryRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        Boolean paymentAuthorized = (Boolean) execution.getVariable("paymentAuthorized");
        Boolean expertApproved = (Boolean) execution.getVariable("expertApproved");
        Boolean eligible = (Boolean) execution.getVariable("eligible");
        Boolean policyValid = (Boolean) execution.getVariable("policyValid");
        Boolean identityVerified = (Boolean) execution.getVariable("identityVerified");

        System.out.println("=== CUSTOMER NOTIFICATION (REST) ===");
        System.out.println("Notifying customer for claim: " + claimId);

        Claim claim = claimRepository.findById(UUID.fromString(claimId)).orElse(null);
        if (claim == null) {
            System.out.println("Claim not found!");
            return;
        }

        // Determine notification message and final status
        String message;
        ClaimStatus finalStatus;

        if (paymentAuthorized != null && paymentAuthorized) {
            // Success path
            Double approvedAmount = (Double) execution.getVariable("approvedAmount");
            String transactionId = (String) execution.getVariable("transactionId");
            message = String.format("Your claim has been approved and paid. Amount: %.2f EUR. Transaction: %s", 
                    approvedAmount, transactionId);
            finalStatus = ClaimStatus.COMPLETED;
        } else if (identityVerified != null && !identityVerified) {
            message = "Your claim has been rejected due to identity verification failure.";
            finalStatus = ClaimStatus.IDENTITY_FAILED;
        } else if (policyValid != null && !policyValid) {
            message = "Your claim has been rejected. Your policy does not cover this claim.";
            finalStatus = ClaimStatus.POLICY_INVALID;
        } else if (eligible != null && !eligible) {
            message = "Your claim has been rejected based on our eligibility rules.";
            finalStatus = ClaimStatus.REJECTED_BY_RULES;
        } else if (expertApproved != null && !expertApproved) {
            message = "Your claim has been rejected after expert review.";
            finalStatus = ClaimStatus.REJECTED;
        } else if (paymentAuthorized != null && !paymentAuthorized) {
            message = "Your claim was approved but payment failed. We will retry.";
            finalStatus = ClaimStatus.PAYMENT_FAILED;
        } else {
            message = "Your claim status has been updated. Please check for details.";
            finalStatus = claim.getStatus();
        }

        // Update claim
        claim.setStatus(finalStatus);
        claimRepository.save(claim);

        // Record in history
        ClaimHistory history = new ClaimHistory();
        history.setClaim(claim);
        history.setPreviousStatus(claim.getStatus());
        history.setNewStatus(finalStatus);
        history.setAction("CUSTOMER_NOTIFIED");
        history.setDetails(message);
        history.setPerformedBy("WORKFLOW");
        claimHistoryRepository.save(history);

        execution.setVariable("notificationSent", true);
        execution.setVariable("notificationMessage", message);
        execution.setVariable("finalStatus", finalStatus.name());

        System.out.println("Notification sent: " + message);
        System.out.println("Final status: " + finalStatus);
    }
}
