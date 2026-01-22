package com.cyterafle.salahsama.claim.processing.flowable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow")
@Tag(name = "Workflow Management", description = "API pour gérer le workflow Flowable")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/start/{claimId}")
    @Operation(summary = "Démarrer le workflow pour une réclamation")
    public ResponseEntity<WorkflowResponse> startWorkflow(@PathVariable String claimId) {
        try {
            ProcessInstance processInstance = workflowService.startClaimProcess(claimId);
            
            return ResponseEntity.ok(new WorkflowResponse(
                    true,
                    "Workflow started successfully",
                    processInstance.getProcessInstanceId(),
                    claimId,
                    "RUNNING",
                    workflowService.getCurrentActivity(processInstance.getProcessInstanceId())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new WorkflowResponse(
                    false,
                    "Failed to start workflow: " + e.getMessage(),
                    null,
                    claimId,
                    "ERROR",
                    null
            ));
        }
    }

    @GetMapping("/status/{claimId}")
    @Operation(summary = "Obtenir le statut du workflow")
    public ResponseEntity<WorkflowStatusResponse> getWorkflowStatus(@PathVariable String claimId) {
        ProcessInstance processInstance = workflowService.getProcessInstance(claimId);
        
        if (processInstance == null) {
            // Check history
            HistoricProcessInstance history = workflowService.getProcessHistory(claimId);
            if (history != null) {
                return ResponseEntity.ok(new WorkflowStatusResponse(
                        claimId,
                        history.getId(),
                        "COMPLETED",
                        "Process completed",
                        null,
                        Map.of()
                ));
            }
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> variables = workflowService.getProcessVariables(processInstance.getProcessInstanceId());
        String currentActivity = workflowService.getCurrentActivity(processInstance.getProcessInstanceId());

        return ResponseEntity.ok(new WorkflowStatusResponse(
                claimId,
                processInstance.getProcessInstanceId(),
                "RUNNING",
                currentActivity,
                workflowService.getActiveTasks(processInstance.getProcessInstanceId())
                        .stream()
                        .map(Task::getName)
                        .collect(Collectors.toList()),
                filterSensitiveVariables(variables)
        ));
    }

    @GetMapping("/variables/{claimId}")
    @Operation(summary = "Obtenir les variables du processus")
    public ResponseEntity<Map<String, Object>> getProcessVariables(@PathVariable String claimId) {
        ProcessInstance processInstance = workflowService.getProcessInstance(claimId);
        
        if (processInstance == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> variables = workflowService.getProcessVariables(processInstance.getProcessInstanceId());
        return ResponseEntity.ok(filterSensitiveVariables(variables));
    }

    @PostMapping("/signal-payment/{claimId}")
    @Operation(summary = "Signaler la réponse de paiement (simule le partenaire)")
    public ResponseEntity<WorkflowResponse> signalPaymentResponse(
            @PathVariable String claimId,
            @RequestBody PaymentSignalRequest request
    ) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("paymentAuthorized", request.authorized());
            variables.put("transactionId", request.transactionId());
            
            workflowService.signalReceiveTask(claimId, variables);
            
            return ResponseEntity.ok(new WorkflowResponse(
                    true,
                    "Payment response signaled successfully",
                    null,
                    claimId,
                    "SIGNAL_SENT",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new WorkflowResponse(
                    false,
                    "Failed to signal payment: " + e.getMessage(),
                    null,
                    claimId,
                    "ERROR",
                    null
            ));
        }
    }

    @DeleteMapping("/cancel/{claimId}")
    @Operation(summary = "Annuler le workflow")
    public ResponseEntity<WorkflowResponse> cancelWorkflow(
            @PathVariable String claimId,
            @RequestParam(defaultValue = "Cancelled by user") String reason
    ) {
        try {
            workflowService.cancelProcess(claimId, reason);
            
            return ResponseEntity.ok(new WorkflowResponse(
                    true,
                    "Workflow cancelled successfully",
                    null,
                    claimId,
                    "CANCELLED",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new WorkflowResponse(
                    false,
                    "Failed to cancel workflow: " + e.getMessage(),
                    null,
                    claimId,
                    "ERROR",
                    null
            ));
        }
    }

    @GetMapping("/tasks/{claimId}")
    @Operation(summary = "Obtenir les tâches actives")
    public ResponseEntity<List<TaskInfo>> getActiveTasks(@PathVariable String claimId) {
        ProcessInstance processInstance = workflowService.getProcessInstance(claimId);
        
        if (processInstance == null) {
            return ResponseEntity.notFound().build();
        }

        List<TaskInfo> tasks = workflowService.getActiveTasks(processInstance.getProcessInstanceId())
                .stream()
                .map(task -> new TaskInfo(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getAssignee(),
                        task.getCreateTime().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(tasks);
    }

    private Map<String, Object> filterSensitiveVariables(Map<String, Object> variables) {
        // Filter out internal Flowable variables
        return variables.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // DTOs
    public record WorkflowResponse(
            boolean success,
            String message,
            String processInstanceId,
            String claimId,
            String status,
            String currentActivity
    ) {}

    public record WorkflowStatusResponse(
            String claimId,
            String processInstanceId,
            String status,
            String currentActivity,
            List<String> activeTasks,
            Map<String, Object> variables
    ) {}

    public record PaymentSignalRequest(
            boolean authorized,
            String transactionId
    ) {}

    public record TaskInfo(
            String taskId,
            String name,
            String description,
            String assignee,
            String createdAt
    ) {}
}
