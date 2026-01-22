package com.cyterafle.salahsama.claim.processing.grpc;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

/**
 * Implémentation gRPC du service de détection de fraude.
 * Note: Dans un projet réel, cette classe étendrait la classe générée par protoc.
 * Pour simplifier, on crée un service REST qui expose les mêmes fonctionnalités.
 */
@Service
public class FraudDetectionGrpcService implements io.grpc.BindableService {

    private final FraudDetectionService fraudDetectionService;

    public FraudDetectionGrpcService(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @Override
    public io.grpc.ServerServiceDefinition bindService() {
        // Définition simplifiée du service gRPC
        // Dans un vrai projet, cela serait généré automatiquement par protoc
        return io.grpc.ServerServiceDefinition.builder("fraud.FraudDetectionService")
                .build();
    }

    // Les méthodes gRPC seront générées par protoc et appelleront fraudDetectionService
}
