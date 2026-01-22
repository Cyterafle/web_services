package com.cyterafle.salahsama.claim.processing.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String policyNumber;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    private PolicyType type;
    
    private Double coverageAmount;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Boolean active;
    
    public enum PolicyType {
        AUTO,
        HOME,
        HEALTH,
        LIFE,
        TRAVEL
    }
    
    public boolean isValid() {
        LocalDate now = LocalDate.now();
        return active != null && active && 
               startDate != null && endDate != null &&
               !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    public boolean coversClaim(String claimType, Double claimedAmount) {
        if (!isValid()) return false;
        if (coverageAmount == null || claimedAmount > coverageAmount) return false;
        return type != null && type.name().equalsIgnoreCase(claimType);
    }
}
