package com.cyterafle.salahsama.claim.processing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour les REST Controllers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ==========================================
    // CLAIM CONTROLLER TESTS
    // ==========================================

    @Test
    void testGetAllClaims() throws Exception {
        mockMvc.perform(get("/api/claims"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetClaimById() throws Exception {
        String claimId = "12345678-1234-1234-1234-123456789012";
        
        mockMvc.perform(get("/api/claims/" + claimId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(claimId));
    }

    @Test
    void testGetClaimByIdNotFound() throws Exception {
        String nonExistentId = "99999999-9999-9999-9999-999999999999";
        
        mockMvc.perform(get("/api/claims/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // CUSTOMER CONTROLLER TESTS
    // ==========================================

    @Test
    void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCustomerById() throws Exception {
        String customerId = "11111111-1111-1111-1111-111111111111";
        
        mockMvc.perform(get("/api/customers/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    // ==========================================
    // ELIGIBILITY TESTS
    // ==========================================

    @Test
    void testEvaluateEligibility() throws Exception {
        String claimId = "12345678-1234-1234-1234-123456789012";
        
        mockMvc.perform(post("/api/eligibility/evaluate/" + claimId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").exists());
    }

    // ==========================================
    // SWAGGER/OPENAPI TESTS
    // ==========================================

    @Test
    void testSwaggerUIAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void testOpenAPIDocsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
