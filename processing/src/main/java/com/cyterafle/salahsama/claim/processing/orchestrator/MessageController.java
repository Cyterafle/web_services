package com.cyterafle.salahsama.claim.processing.orchestrator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.Customer;
import com.cyterafle.salahsama.claim.processing.repository.CustomerRepository;
import com.cyterafle.salahsama.claim.processing.rest.controller.ClaimController;

@Controller
public class MessageController {

    private final CustomerRepository customerRepository;

    public MessageController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @MessageMapping("/init")
    @SendTo("/response/claim")
    public WorkflowStatusMessage init(ClaimController.ClaimSubmissionRequest request) throws Exception {
        Thread.sleep(1000);
        Claim claim = DTOToEntity(request);
        List<Step> steps = new ArrayList<>();
        Step step1 = new Step(0, "Test", "Not started");
        steps.add(step1);
        Step step2 = new Step(1, "Test", "Not started");
        steps.add(step2);
        return new WorkflowStatusMessage(steps, claim);
    }

    private Claim DTOToEntity(ClaimController.ClaimSubmissionRequest request){
        Customer customer = customerRepository.findById(UUID.fromString(request.customerId()))
                .orElse(null);
        
        Claim claim = new Claim();
        claim.setCustomer(customer);
        claim.setId(UUID.randomUUID());
        claim.setPolicyNumber(request.policyNumber());
        claim.setClaimType(request.claimType());
        claim.setClaimedAmount(request.claimedAmount());
        claim.setDescription(request.description());
        return claim;
    }
}