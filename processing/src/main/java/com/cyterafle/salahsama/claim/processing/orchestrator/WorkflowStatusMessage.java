package com.cyterafle.salahsama.claim.processing.orchestrator;

import java.util.List;
import java.util.UUID;

import com.cyterafle.salahsama.claim.processing.entity.Claim;

public class WorkflowStatusMessage {
    private UUID workflowId;
    private String currentStep;
    private String status;
    private List<Step> steps;
    private Claim claim;

    public WorkflowStatusMessage(List<Step> steps, Claim claim){
        workflowId = UUID.randomUUID();
        this.steps = steps;
        this.claim = claim;
    }

    public Claim getClaim(){
        return claim;
    }

    public UUID getWorkflowId(){
        return workflowId;
    }

    public String getCurrentStep(){
        return currentStep;
    }

    public String getStatus(){
        return status;
    }

    public List<Step> getSteps(){
        return steps;
    }

    public void setClaim(Claim claim){
        this.claim = claim;
    }

    public void setWorkflowId(UUID workflowId){
        this.workflowId = workflowId;
    }

    public void setCurrentStep(String currentStep){
        this.currentStep = currentStep;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setSteps(List<Step> steps){
        this.steps = steps;
    }
}
