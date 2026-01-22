package com.cyterafle.salahsama.claim.processing;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.entity.Customer;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import com.cyterafle.salahsama.claim.processing.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les Repositories et Entités
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryUnitTest {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // ==========================================
    // CUSTOMER REPOSITORY TESTS
    // ==========================================

    @Test
    void testFindCustomerById() {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        
        Optional<Customer> customer = customerRepository.findById(customerId);
        
        assertTrue(customer.isPresent());
        assertEquals("John", customer.get().getName());
        assertEquals("Doe", customer.get().getSurname());
        assertTrue(customer.get().getVerified());
    }

    @Test
    void testFindAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        
        assertFalse(customers.isEmpty());
        assertTrue(customers.size() >= 4);
    }

    @Test
    void testCustomerNotFound() {
        UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        
        Optional<Customer> customer = customerRepository.findById(nonExistentId);
        
        assertFalse(customer.isPresent());
    }

    // ==========================================
    // CLAIM REPOSITORY TESTS
    // ==========================================

    @Test
    void testFindClaimById() {
        UUID claimId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        
        Optional<Claim> claim = claimRepository.findById(claimId);
        
        assertTrue(claim.isPresent());
        assertEquals("POL-AUTO-001", claim.get().getPolicyNumber());
        assertEquals("AUTO", claim.get().getClaimType());
    }

    @Test
    void testFindAllClaims() {
        List<Claim> claims = claimRepository.findAll();
        
        assertFalse(claims.isEmpty());
        assertTrue(claims.size() >= 3);
    }

    @Test
    void testFindClaimsByCustomerId() {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        
        List<Claim> claims = claimRepository.findByCustomerId(customerId);
        
        assertFalse(claims.isEmpty());
        claims.forEach(claim -> 
            assertEquals(customerId, claim.getCustomer().getId())
        );
    }

    // ==========================================
    // ENTITY RELATIONSHIP TESTS
    // ==========================================

    @Test
    void testClaimCustomerRelationship() {
        UUID claimId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        
        Optional<Claim> claimOpt = claimRepository.findById(claimId);
        assertTrue(claimOpt.isPresent());
        
        Claim claim = claimOpt.get();
        assertNotNull(claim.getCustomer());
        assertEquals("John", claim.getCustomer().getName());
    }

    @Test
    void testClaimPolicyRelationship() {
        UUID claimId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        
        Optional<Claim> claimOpt = claimRepository.findById(claimId);
        assertTrue(claimOpt.isPresent());
        
        Claim claim = claimOpt.get();
        assertNotNull(claim.getPolicy());
        assertEquals("POL-AUTO-001", claim.getPolicy().getPolicyNumber());
    }
}
