package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.Customer;
import com.cyterafle.salahsama.claim.processing.entity.Policy;
import com.cyterafle.salahsama.claim.processing.repository.CustomerRepository;
import com.cyterafle.salahsama.claim.processing.repository.PolicyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policy Management", description = "API pour gérer les polices d'assurance")
public class PolicyController {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public PolicyController(PolicyRepository policyRepository, CustomerRepository customerRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les polices")
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une police par ID")
    public ResponseEntity<Policy> getPolicy(@PathVariable String id) {
        return policyRepository.findById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{policyNumber}")
    @Operation(summary = "Récupérer une police par numéro")
    public ResponseEntity<Policy> getPolicyByNumber(@PathVariable String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Récupérer les polices d'un client")
    public List<Policy> getPoliciesByCustomer(@PathVariable String customerId) {
        return policyRepository.findByCustomerId(UUID.fromString(customerId));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle police")
    public ResponseEntity<?> createPolicy(@RequestBody PolicyCreateRequest request) {
        Customer customer = customerRepository.findById(UUID.fromString(request.customerId()))
                .orElse(null);

        if (customer == null) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        Policy policy = new Policy();
        policy.setPolicyNumber(request.policyNumber());
        policy.setCustomer(customer);
        policy.setType(Policy.PolicyType.valueOf(request.type().toUpperCase()));
        policy.setCoverageAmount(request.coverageAmount());
        policy.setStartDate(request.startDate());
        policy.setEndDate(request.endDate());
        policy.setActive(true);

        Policy saved = policyRepository.save(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activer une police")
    public ResponseEntity<Policy> activatePolicy(@PathVariable String id) {
        return policyRepository.findById(UUID.fromString(id))
                .map(policy -> {
                    policy.setActive(true);
                    return ResponseEntity.ok(policyRepository.save(policy));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver une police")
    public ResponseEntity<Policy> deactivatePolicy(@PathVariable String id) {
        return policyRepository.findById(UUID.fromString(id))
                .map(policy -> {
                    policy.setActive(false);
                    return ResponseEntity.ok(policyRepository.save(policy));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DTO
    public record PolicyCreateRequest(
            String customerId,
            String policyNumber,
            String type,
            Double coverageAmount,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    ) {}
}
