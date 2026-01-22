package com.cyterafle.salahsama.claim.processing.soap;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cyterafle.salahsama.claim.processing.entity.Policy;
import com.cyterafle.salahsama.claim.processing.repository.PolicyRepository;
import com.cyterafle.salahsama.claim.processing.soap.generated.*;

@Endpoint
public class PolicyValidationEndpoint {

    private static final String NAMESPACE_URI = "http://cyterafle.com/claim/soap/policy";

    @Autowired
    private PolicyRepository policyRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "validatePolicyRequest")
    @ResponsePayload
    public ValidatePolicyResponse validatePolicy(@RequestPayload ValidatePolicyRequest request) {
        ValidatePolicyResponse response = new ValidatePolicyResponse();
        
        Optional<Policy> policyOpt = policyRepository.findByPolicyNumber(request.getPolicyNumber());
        
        if (policyOpt.isEmpty()) {
            response.setValid(false);
            response.setMessage("Policy not found with number: " + request.getPolicyNumber());
            response.setErrorCode("POLICY_NOT_FOUND");
            return response;
        }
        
        Policy policy = policyOpt.get();
        
        // Vérifier que la police appartient au client
        if (!policy.getCustomer().getId().toString().equals(request.getCustomerId())) {
            response.setValid(false);
            response.setMessage("Policy does not belong to this customer");
            response.setErrorCode("POLICY_CUSTOMER_MISMATCH");
            return response;
        }
        
        // Vérifier que la police est valide (dates et active)
        if (!policy.isValid()) {
            response.setValid(false);
            response.setMessage("Policy is not active or has expired");
            response.setErrorCode("POLICY_INACTIVE");
            return response;
        }
        
        // Vérifier que la police couvre le type de réclamation
        if (!policy.getType().name().equalsIgnoreCase(request.getClaimType())) {
            response.setValid(false);
            response.setMessage("Policy type " + policy.getType() + " does not cover claim type " + request.getClaimType());
            response.setErrorCode("CLAIM_TYPE_NOT_COVERED");
            return response;
        }
        
        // Vérifier que le montant ne dépasse pas la couverture
        if (request.getClaimedAmount() > policy.getCoverageAmount()) {
            response.setValid(false);
            response.setMessage("Claimed amount " + request.getClaimedAmount() + 
                              " exceeds coverage limit " + policy.getCoverageAmount());
            response.setErrorCode("AMOUNT_EXCEEDS_COVERAGE");
            return response;
        }
        
        // Police valide
        response.setValid(true);
        response.setPolicyId(policy.getId().toString());
        response.setPolicyType(policy.getType().name());
        response.setCoverageAmount(policy.getCoverageAmount());
        response.setMessage("Policy is valid and covers this claim");
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPolicyDetailsRequest")
    @ResponsePayload
    public GetPolicyDetailsResponse getPolicyDetails(@RequestPayload GetPolicyDetailsRequest request) {
        GetPolicyDetailsResponse response = new GetPolicyDetailsResponse();
        
        Optional<Policy> policyOpt = policyRepository.findByPolicyNumber(request.getPolicyNumber());
        
        if (policyOpt.isEmpty()) {
            response.setFound(false);
            response.setMessage("Policy not found");
            return response;
        }
        
        Policy policy = policyOpt.get();
        
        PolicyInfo policyInfo = new PolicyInfo();
        policyInfo.setPolicyId(policy.getId().toString());
        policyInfo.setPolicyNumber(policy.getPolicyNumber());
        policyInfo.setCustomerId(policy.getCustomer().getId().toString());
        policyInfo.setType(policy.getType().name());
        policyInfo.setCoverageAmount(policy.getCoverageAmount());
        policyInfo.setActive(policy.getActive());
        
        // Conversion des dates
        try {
            if (policy.getStartDate() != null) {
                policyInfo.setStartDate(
                    javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(policy.getStartDate().toString())
                );
            }
            if (policy.getEndDate() != null) {
                policyInfo.setEndDate(
                    javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(policy.getEndDate().toString())
                );
            }
        } catch (javax.xml.datatype.DatatypeConfigurationException e) {
            // Log error and continue without dates
            response.setMessage("Policy found (date conversion error)");
        }
        
        response.setFound(true);
        response.setPolicy(policyInfo);
        response.setMessage("Policy found");
        
        return response;
    }
}
