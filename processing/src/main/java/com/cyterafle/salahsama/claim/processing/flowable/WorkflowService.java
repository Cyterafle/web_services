package com.cyterafle.salahsama.claim.processing.flowable;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.entity.ClaimStatus;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final ClaimRepository claimRepository;

    private static final String PROCESS_DEFINITION_KEY = "insuranceClaimProcess";

    public WorkflowService(
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService,
            ClaimRepository claimRepository
    ) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.claimRepository = claimRepository;
    }

    /**
     * Start a new claim processing workflow
     */
    public ProcessInstance startClaimProcess(String claimId) {
        Claim claim = claimRepository.findById(UUID.fromString(claimId))
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        // Prepare process variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("claimId", claimId);
        variables.put("customerId", claim.getCustomer().getId().toString());
        variables.put("policyNumber", claim.getPolicyNumber());
        variables.put("claimType", claim.getClaimType());
        variables.put("claimedAmount", claim.getClaimedAmount());
        variables.put("description", claim.getDescription());
        
        // Initialize status variables
        variables.put("identityVerified", false);
        variables.put("policyValid", false);
        variables.put("fraudRiskLevel", "UNKNOWN");
        variables.put("eligible", false);
        variables.put("expertApproved", false);
        variables.put("paymentAuthorized", false);
        variables.put("documentsValid", true);  // Assume documents are valid by default
        variables.put("approvedAmount", claim.getClaimedAmount());  // Initialize with claimed amount

        // Start the process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY,
                claimId,  // Business key
                variables
        );

        // Update claim with process instance ID
        claim.setProcessInstanceId(processInstance.getProcessInstanceId());
        claimRepository.save(claim);

        return processInstance;
    }

    /**
     * Get process instance by claim ID
     */
    public ProcessInstance getProcessInstance(String claimId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(claimId)
                .singleResult();
    }

    /**
     * Get process variables
     */
    public Map<String, Object> getProcessVariables(String processInstanceId) {
        return runtimeService.getVariables(processInstanceId);
    }

    /**
     * Set a process variable
     */
    public void setProcessVariable(String processInstanceId, String variableName, Object value) {
        runtimeService.setVariable(processInstanceId, variableName, value);
    }

    /**
     * Get active tasks for a process
     */
    public List<Task> getActiveTasks(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
    }

    /**
     * Complete a user task
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }

    /**
     * Signal a waiting receive task (for message events)
     */
    public void signalReceiveTask(String claimId, Map<String, Object> variables) {
        ProcessInstance processInstance = getProcessInstance(claimId);
        if (processInstance != null) {
            // Find the receive task execution
            runtimeService.createExecutionQuery()
                    .processInstanceId(processInstance.getProcessInstanceId())
                    .activityId("receivePaymentResponse")
                    .list()
                    .forEach(execution -> {
                        runtimeService.trigger(execution.getId(), variables);
                    });
        }
    }

    /**
     * Check if process is completed
     */
    public boolean isProcessCompleted(String claimId) {
        ProcessInstance processInstance = getProcessInstance(claimId);
        return processInstance == null || processInstance.isEnded();
    }

    /**
     * Get process history
     */
    public HistoricProcessInstance getProcessHistory(String claimId) {
        return historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(claimId)
                .singleResult();
    }

    /**
     * Get current activity name
     */
    public String getCurrentActivity(String processInstanceId) {
        List<Task> tasks = getActiveTasks(processInstanceId);
        if (!tasks.isEmpty()) {
            return tasks.get(0).getName();
        }
        
        // Check if waiting on receive task
        var executions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .list();
        
        for (var execution : executions) {
            if (execution.getActivityId() != null) {
                return execution.getActivityId();
            }
        }
        
        return "Unknown";
    }

    /**
     * Cancel a process
     */
    public void cancelProcess(String claimId, String reason) {
        ProcessInstance processInstance = getProcessInstance(claimId);
        if (processInstance != null) {
            runtimeService.deleteProcessInstance(
                    processInstance.getProcessInstanceId(),
                    reason
            );
        }
    }
}
