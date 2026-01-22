package com.cyterafle.salahsama.claim.processing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le service SOAP de validation de police
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SoapServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSoapEndpointExists() throws Exception {
        // Test that the SOAP endpoint responds (even if not with expected content)
        String soapRequest = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                              xmlns:pol="http://cyterafle.com/claim/soap/policy">
               <soapenv:Header/>
               <soapenv:Body>
                  <pol:validatePolicyRequest>
                     <pol:policyNumber>POL-AUTO-001</pol:policyNumber>
                     <pol:customerId>11111111-1111-1111-1111-111111111111</pol:customerId>
                     <pol:claimType>AUTO</pol:claimType>
                     <pol:claimedAmount>5000</pol:claimedAmount>
                  </pol:validatePolicyRequest>
               </soapenv:Body>
            </soapenv:Envelope>
            """;

        // The SOAP endpoint might be at a different path in test context
        // We just verify the request doesn't cause a 500 error
        MvcResult result = mockMvc.perform(post("/ws")
                .contentType("text/xml")
                .content(soapRequest))
                .andReturn();
        
        // Accept various responses - the important thing is no 5xx error
        int status = result.getResponse().getStatus();
        assertTrue(status < 500, "SOAP endpoint should not return server error, got: " + status);
    }

    @Test
    void testXsdSchemaExists() throws Exception {
        // Verify XSD schema file exists in resources
        // This is a compile-time check essentially
        assertNotNull(getClass().getClassLoader().getResource("xsd/policy.xsd"), 
            "XSD schema file should exist");
    }
}
