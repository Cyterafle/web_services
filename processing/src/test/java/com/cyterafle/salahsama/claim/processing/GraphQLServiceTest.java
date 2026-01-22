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
 * Tests pour le service GraphQL de Claim Tracking
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GraphQLServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGraphQLEndpointAvailable() throws Exception {
        String graphqlQuery = """
            {
                "query": "{ claims { id claimType status } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(graphqlQuery))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetClaimById() throws Exception {
        String claimId = "12345678-1234-1234-1234-123456789012";
        String graphqlQuery = """
            {
                "query": "query { claim(id: \\"%s\\") { id policyNumber claimType status } }"
            }
            """.formatted(claimId);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(graphqlQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.claim.id").value(claimId));
    }

    @Test
    void testGetClaimHistory() throws Exception {
        String claimId = "12345678-1234-1234-1234-123456789012";
        String graphqlQuery = """
            {
                "query": "query { claim(id: \\"%s\\") { id history { action details } } }"
            }
            """.formatted(claimId);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(graphqlQuery))
                .andExpect(status().isOk());
    }

    @Test
    void testGetClaimWithCustomer() throws Exception {
        String claimId = "12345678-1234-1234-1234-123456789012";
        String graphqlQuery = """
            {
                "query": "query { claim(id: \\"%s\\") { id customer { id name surname } } }"
            }
            """.formatted(claimId);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(graphqlQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.claim.customer.name").value("John"));
    }
}
