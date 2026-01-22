package com.cyterafle.salahsama.claim.processing.flowable.delegates;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Send Task: Payment Request to Payment Partner
 * Simulates sending a payment request to an external partner
 */
@Component("paymentRequestDelegate")
public class PaymentRequestDelegate implements JavaDelegate {

    private final ClaimRepository claimRepository;

    public PaymentRequestDelegate(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String claimId = (String) execution.getVariable("claimId");
        String customerId = (String) execution.getVariable("customerId");
        Double approvedAmount = (Double) execution.getVariable("approvedAmount");

        System.out.println("=== PAYMENT REQUEST (to Partner Pool) ===");
        System.out.println("Sending payment request for claim: " + claimId);
        System.out.println("Amount: " + approvedAmount);

        // Generate payment request ID
        String paymentRequestId = "PAY-REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Store payment request details
        execution.setVariable("paymentRequestId", paymentRequestId);
        execution.setVariable("paymentAmount", approvedAmount);
        execution.setVariable("paymentCustomerId", customerId);

        // In a real scenario, this would send a message to the Payment Partner pool
        // For simulation, we'll auto-trigger the partner process
        System.out.println("Payment request sent: " + paymentRequestId);
        
        // Simulate partner response (in real scenario, this would be asynchronous)
        // The receivePaymentResponse task will wait for a signal
        boolean paymentAuthorized = Math.random() > 0.05; // 95% success rate
        String transactionId = paymentAuthorized 
                ? "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : null;

        execution.setVariable("paymentAuthorized", paymentAuthorized);
        execution.setVariable("transactionId", transactionId);
        
        System.out.println("Partner response: " + (paymentAuthorized ? "AUTHORIZED" : "DECLINED"));
    }
}
