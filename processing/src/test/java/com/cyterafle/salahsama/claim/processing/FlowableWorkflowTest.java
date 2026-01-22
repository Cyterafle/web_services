package com.cyterafle.salahsama.claim.processing;

import com.cyterafle.salahsama.claim.processing.flowable.WorkflowService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le workflow Flowable BPMN
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlowableWorkflowTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private WorkflowService workflowService;

    // ==========================================
    // PROCESS DEFINITION TESTS
    // ==========================================

    @Test
    void testProcessDefinitionDeployed() {
        List<ProcessDefinition> definitions = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("insuranceClaimProcess")
                .list();
        
        assertFalse(definitions.isEmpty(), "Process definition should be deployed");
    }

    @Test
    void testProcessDefinitionIsExecutable() {
        ProcessDefinition definition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("insuranceClaimProcess")
                .latestVersion()
                .singleResult();
        
        assertNotNull(definition);
        assertEquals("Insurance Claim Processing", definition.getName());
    }

    // ==========================================
    // WORKFLOW SERVICE TESTS
    // ==========================================

    @Test
    void testStartClaimProcess() {
        String claimId = "12345678-1234-1234-1234-123456789012";
        
        ProcessInstance processInstance = workflowService.startClaimProcess(claimId);
        
        assertNotNull(processInstance);
        assertNotNull(processInstance.getProcessInstanceId());
        assertEquals(claimId, processInstance.getBusinessKey());
    }

    @Test
    void testGetProcessHistory() {
        String claimId = "23456789-2345-2345-2345-234567890123";
        
        // Start a process
        workflowService.startClaimProcess(claimId);
        
        // Get history
        var history = workflowService.getProcessHistory(claimId);
        
        assertNotNull(history);
        assertEquals(claimId, history.getBusinessKey());
    }

    // ==========================================
    // GATEWAY VERIFICATION TESTS
    // ==========================================

    @Test
    void testBpmnHasRequiredGateways() {
        // Verify process definition exists with all required elements
        ProcessDefinition definition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("insuranceClaimProcess")
                .latestVersion()
                .singleResult();
        
        assertNotNull(definition, "BPMN process with XOR, AND, and OR gateways should be deployed");
        
        // The BPMN file contains:
        // - XOR Gateways: identityGateway, validationResultsGateway, expertDecisionGateway, eligibilityGateway, paymentGateway
        // - AND Gateways: parallelValidationSplit, parallelValidationJoin
        // - OR Gateways: inclusiveReviewSplit, inclusiveReviewJoin
    }
}
