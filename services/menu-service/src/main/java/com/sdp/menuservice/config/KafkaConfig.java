package com.sdp.menuservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.menu-events}")
    private String menuEventsTopic;

    @Bean
    public NewTopic menuEventsTopic() {
        return TopicBuilder.name(menuEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
