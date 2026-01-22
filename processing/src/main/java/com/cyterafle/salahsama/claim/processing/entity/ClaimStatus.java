package com.cyterafle.salahsama.claim.processing.entity;

public enum ClaimStatus {
    SUBMITTED,              // Réclamation soumise
    IDENTITY_VERIFIED,      // Identité vérifiée
    IDENTITY_FAILED,        // Échec vérification identité
    POLICY_VALIDATED,       // Police validée
    POLICY_INVALID,         // Police invalide
    FRAUD_CHECK_LOW,        // Risque fraude faible
    FRAUD_CHECK_MEDIUM,     // Risque fraude moyen
    FRAUD_CHECK_HIGH,       // Risque fraude élevé
    ELIGIBLE,               // Éligible au traitement
    REJECTED_BY_RULES,      // Rejeté par les règles métier
    DOCUMENTS_REQUESTED,    // Documents demandés
    DOCUMENTS_RECEIVED,     // Documents reçus
    DOCUMENTS_INVALID,      // Documents invalides
    EXPERT_REVIEW,          // En cours d'examen expert
    APPROVED,               // Approuvé
    REJECTED,               // Rejeté
    COMPENSATION_CALCULATED,// Compensation calculée
    PAYMENT_AUTHORIZED,     // Paiement autorisé
    PAYMENT_FAILED,         // Échec paiement
    COMPLETED,              // Terminé
    SUSPENDED               // Suspendu
}
