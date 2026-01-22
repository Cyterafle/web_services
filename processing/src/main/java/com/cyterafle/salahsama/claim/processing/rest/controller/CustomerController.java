package com.cyterafle.salahsama.claim.processing.rest.controller;

import com.cyterafle.salahsama.claim.processing.entity.Customer;
import com.cyterafle.salahsama.claim.processing.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "API pour gérer les clients")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les clients")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par ID")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        return customerRepository.findById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau client")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        customer.setVerified(false);
        Customer saved = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String id,
            @RequestBody Customer customerDetails
    ) {
        return customerRepository.findById(UUID.fromString(id))
                .map(customer -> {
                    customer.setName(customerDetails.getName());
                    customer.setSurname(customerDetails.getSurname());
                    customer.setMail(customerDetails.getMail());
                    customer.setPhone(customerDetails.getPhone());
                    customer.setAddress(customerDetails.getAddress());
                    return ResponseEntity.ok(customerRepository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        return customerRepository.findById(UUID.fromString(id))
                .map(customer -> {
                    customerRepository.delete(customer);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
