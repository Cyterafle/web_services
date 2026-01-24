package com.cyterafle.salahsama.claim.processing.orchestrator;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/response");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Raw WebSocket endpoint (for tools like Postman that use plain WebSocket/STOMP)
        registry.addEndpoint("/websocket-raw")
                .setAllowedOrigins("http://localhost:8000", "http://localhost:3000", "http://localhost:8080", "localhost");

        // SockJS-enabled endpoint (for browser clients using SockJS)
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("http://localhost:8000", "http://localhost:3000", "http://localhost:8080", "localhost")
                .withSockJS();
    }
}
