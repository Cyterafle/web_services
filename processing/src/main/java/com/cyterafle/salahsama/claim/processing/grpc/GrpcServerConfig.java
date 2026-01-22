package com.cyterafle.salahsama.claim.processing.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private Server server;

    private final FraudDetectionGrpcService fraudDetectionGrpcService;

    public GrpcServerConfig(FraudDetectionGrpcService fraudDetectionGrpcService) {
        this.fraudDetectionGrpcService = fraudDetectionGrpcService;
    }

    @PostConstruct
    public void startGrpcServer() throws IOException {
        server = ServerBuilder.forPort(grpcPort)
                .addService(fraudDetectionGrpcService)
                .build()
                .start();
        
        System.out.println("gRPC Server started on port " + grpcPort);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            if (server != null) {
                server.shutdown();
            }
        }));
    }

    @PreDestroy
    public void stopGrpcServer() {
        if (server != null) {
            server.shutdown();
        }
    }
}
