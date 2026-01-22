package com.cyterafle.salahsama.claim.processing.flowable;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;

@Configuration
public class FlowableConfig {

    /**
     * Configure Flowable engine with async executor disabled for simplicity
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurationConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setAsyncExecutorActivate(false);
            engineConfiguration.setDatabaseSchemaUpdate("true");
        };
    }
}
