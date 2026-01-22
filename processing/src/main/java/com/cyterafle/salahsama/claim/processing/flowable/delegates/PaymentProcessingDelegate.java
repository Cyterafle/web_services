package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service Task: Payment Processing by Payment Partner
 * This runs in the Payment Partner Pool
 */
@Component("paymentProcessingDelegate")
public class PaymentProcessingDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String paymentRequestId = (String) execution.getVariable("paymentRequestId");
        Double amount = (Double) execution.getVariable("paymentAmount");

        System.out.println("=== PAYMENT PARTNER: Processing Payment ===");
        System.out.println("Request ID: " + paymentRequestId);
        System.out.println("Amount: " + amount);

        // Simulate payment processing
        // In a real system, this would connect to a payment gateway
        boolean success = Math.random() > 0.05; // 95% success rate
        
        String transactionId = null;
        String failureReason = null;
        
        if (success) {
            transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Payment successful. Transaction ID: " + transactionId);
        } else {
            failureReason = "Insufficient funds or network error";
            System.out.println("Payment failed: " + failureReason);
        }

        // Set response variables
        execution.setVariable("partnerPaymentSuccess", success);
        execution.setVariable("partnerTransactionId", transactionId);
        execution.setVariable("partnerFailureReason", failureReason);
    }
}
